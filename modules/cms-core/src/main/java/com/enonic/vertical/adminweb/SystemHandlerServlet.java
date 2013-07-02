/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.jdom.JDOMException;
import org.joda.time.format.PeriodFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.product.ProductVersion;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.time.DateTimeFormatters;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.core.tools.DataSourceInfoResolver;
import com.enonic.cms.core.vacuum.ProgressInfo;
import com.enonic.cms.core.vacuum.VacuumService;

/**
 *
 */
public class SystemHandlerServlet
    extends AdminHandlerBaseServlet
{
    @Autowired
    private DataSourceInfoResolver datasourceInfoResolver;

    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private ConfigProperties configurationProperties;

    @Autowired
    private TimeService timeService;

    @Autowired
    private VacuumService vacuumService;

    @Autowired
    private CacheManager cacheManager;

    private ObjectMapper jacksonObjectMapper;

    public SystemHandlerServlet()
    {
        jacksonObjectMapper = new ObjectMapper().configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalEngineException, VerticalAdminException
    {

        if ( "page".equals( operation ) )
        {
            handlerPage( request, response, session, admin, formItems, operation );
        }
        else if ( "cleanReadLogs".equals( operation ) )
        {
            handlerCleanReadLogs( admin, request, response );
        }
        else if ( "cleanUnusedContent".equals( operation ) )
        {
            handlerCleanUnusedContent( admin, request, response );
        }
        else if ( "clearcache".equals( operation ) )
        {
            clearCache( request, response, formItems );
        }
        else if ( "optimizeIndex".equals( operation ) )
        {
            optimizeIndex( request, response, formItems );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    public void handlerPage( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, String operation )
        throws VerticalEngineException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Source xslSource = AdminStore.getStylesheet( session, "system_page.xsl" );

        String mode = formItems.getString( "mode" );

        Document doc = XMLTool.createDocument( "vertical" );
        Element root = doc.getDocumentElement();

        try
        {
            if ( mode.equals( "system" ) )
            {
                root.appendChild( buildJavaInfo( doc ) );
                root.setAttribute( "bootTime", DateTimeFormatters.DATE_TIME.print( timeService.bootTime() ) );
                root.setAttribute( "upTime", PeriodFormat.wordBased().print( timeService.upTime() ) );
                root.setAttribute( "version", ProductVersion.getVersion() );
                root.setAttribute( "modelVersion", String.valueOf( this.upgradeService.getCurrentModelNumber() ) );
                root.setAttribute( "isCleanInProgress", String.valueOf( vacuumService.getProgressInfo().isInProgress() ) );
                root.appendChild( buildComponentsInfo( doc ) );
            }
            else if ( mode.equals( "java_properties" ) )
            {
                XMLTool.mergeDocuments( doc, createPropertiesInfoDocument(), true );

            }

            Source xmlSource = new DOMSource( doc );

            // parameters
            ExtendedMap xslParams = new ExtendedMap();

            xslParams.put( "page", request.getParameter( "page" ) );
            xslParams.put( "selectedtabpage", request.getParameter( "selectedtabpage" ) );
            xslParams.put( "mode", mode );

            xslParams.put( "selectedoperation", request.getParameter( "selectedoperation" ) );
            xslParams.put( "selectedcachename", request.getParameter( "selectedcachename" ) );

            addAccessLevelParameters( user, xslParams );

            transformXML( session, response.getWriter(), xmlSource, xslSource, xslParams );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin( "XSLT error: %t", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin( "I/O error: %t", e );
        }
    }

    private Document createPropertiesInfoDocument()
    {
        PropertiesInfoModelFactory propertiesInfoModelFactory =
            new PropertiesInfoModelFactory( datasourceInfoResolver, configurationProperties );
        PropertiesInfoModel infoModel = propertiesInfoModelFactory.createSystemPropertiesModel();

        final Document doc;
        try
        {
            doc = JDOMUtil.toW3CDocument( infoModel.toXML() );
        }
        catch ( JDOMException e )
        {
            throw new VerticalAdminException( "Failed to create system-properties document" );
        }

        return doc;
    }

    /**
     * Clean read logs.
     */
    private void handlerCleanReadLogs( AdminService admin, HttpServletRequest request, HttpServletResponse response )
    {
        if ( "getprogress".equals( request.getParameter( "subop" ) ) )
        {
            renderGetProgress( response );
        }
        else
        {
            vacuumService.cleanReadLogs();
        }
    }

    /**
     * Clean unused content.
     */
    private void handlerCleanUnusedContent( AdminService admin, HttpServletRequest request, HttpServletResponse response )
    {
        if ( "getprogress".equals( request.getParameter( "subop" ) ) )
        {
            renderGetProgress( response );
        }
        else
        {
            vacuumService.cleanUnusedContent( );
        }
    }

    private void renderGetProgress( final HttpServletResponse response )
    {
        final ProgressInfo cleanUnusedContentProgressInfo = vacuumService.getProgressInfo();

        try
        {
            response.setHeader( "Content-Type", "application/json; charset=UTF-8" );

            final String json = jacksonObjectMapper.writeValueAsString( cleanUnusedContentProgressInfo );
            response.getWriter().println( json );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to transform objects to JSON: " + e.getMessage(), e );
        }
    }

    private Element buildJavaInfo( Document doc )
    {

        Element javaEl = doc.createElement( "java" );
        javaEl.setAttribute( "version", findJavaVersion() );

        Element memoryEl = XMLTool.createElement( javaEl, "memory" );
        Element heapEl = XMLTool.createElement( memoryEl, "heap" );

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        heapEl.setAttribute( "max", String.valueOf( heap.getMax() ) );
        heapEl.setAttribute( "committed", String.valueOf( heap.getCommitted() ) );
        heapEl.setAttribute( "used", String.valueOf( heap.getUsed() ) );

        MemoryUsage nonheap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        Element nonheapEl = XMLTool.createElement( memoryEl, "nonheap" );
        nonheapEl.setAttribute( "max", String.valueOf( nonheap.getMax() ) );
        nonheapEl.setAttribute( "committed", String.valueOf( nonheap.getCommitted() ) );
        nonheapEl.setAttribute( "used", String.valueOf( nonheap.getUsed() ) );

        return javaEl;
    }

    /**
     * Append component version informations.
     */
    private Element buildComponentsInfo( Document doc )
    {
        Element root = doc.createElement( "components" );
        root.appendChild( buildComponentInfo( doc, "Saxon", findSaxonVersion() ) );
        return root;
    }

    /**
     * Append component version information.
     */
    private Element buildComponentInfo( Document doc, String name, String version )
    {
        Element root = doc.createElement( "component" );
        root.setAttribute( "name", name );
        root.setAttribute( "version", version );
        return root;
    }

    /**
     * Find java version.
     */
    private String findJavaVersion()
    {
        return System.getProperty( "java.vm.version" );
    }

    /**
     * Find saxon version.
     */
    private String findSaxonVersion()
    {
        return net.sf.saxon.Version.getProductVersion();
    }

    private void clearCache( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        final String cacheName = formItems.getString( "cacheName" );
        final CacheFacade cache = cacheManager.getCache( cacheName );
        if ( cache != null )
        {
            cache.removeAll();
        }

        URL referer = new URL( request.getHeader( "referer" ) );
        referer.setParameter( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        referer.setParameter( "selectedoperation", formItems.getString( "selectedoperation", formItems.getString( "op", "" ) ) );
        referer.setParameter( "selectedcachename", formItems.getString( "selectedcachename", formItems.getString( "cacheName", "" ) ) );
        referer.setParameter( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );

        redirectClientToURL( referer, response );
    }

    private void optimizeIndex( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        contentIndexService.optimize();

        URL referrer = new URL( request.getHeader( "referer" ) );
        referrer.setParameter( "selectedoperation", "optimizeindex" );
        redirectClientToURL( referrer, response );
    }

    public void setPageCacheService( PageCacheService value )
    {
        this.pageCacheService = value;
    }

    public void setDatasourceInfoResolver( DataSourceInfoResolver datasourceInfoResolver )
    {
        this.datasourceInfoResolver = datasourceInfoResolver;
    }
}
