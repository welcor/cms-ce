/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.context;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.portal.PageRequestType;
import com.enonic.cms.core.portal.datasource.DataSourceType;
import com.enonic.cms.core.portal.datasource.executor.DataSourceExecutorContext;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.store.dao.GroupDao;

@Component
public final class DataSourcesContextXmlCreator
    implements InitializingBean
{
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Autowired
    private GroupDao groupDao;

    private UserContextXmlCreator userContextXmlCreator;

    private QueryStringContextXmlCreator queryStringContextXmlCreator;

    private StylesContextXmlCreator stylesContextXmlCreator;

    private final WindowContextXmlCreator windowContextXmlCreator = new WindowContextXmlCreator();

    private final PageContextXmlCreator pageContextXmlCreator = new PageContextXmlCreator();

    public void afterPropertiesSet()
    {
        stylesContextXmlCreator = new StylesContextXmlCreator( resourceService );
        queryStringContextXmlCreator = new QueryStringContextXmlCreator( siteURLResolver );
        userContextXmlCreator = new UserContextXmlCreator( groupDao );
    }

    public Element createContextElement( final DataSourceExecutorContext context )
    {
        Element contextElem = new Element( "context" );

        SiteEntity site = context.getSite();

        SiteProperties siteProperties = context.getSiteProperties();

        LanguageEntity language = context.getLanguage();

        final HttpServletRequest request = context.getHttpRequest();

        // Language context
        contextElem.setAttribute( "languagecode", language.getCode() );

        // Querystring context
        Element queryStringElem =
            queryStringContextXmlCreator.createQueryStringElement( context.getHttpRequest(), context.getOriginalSitePath(),
                                                                   context.getRequestParameters() );
        contextElem.addContent( queryStringElem );

        // render mode
        String mode = "live";

        if ( RenderTrace.isTraceOn() )
        {
            mode = "edit";
        }
        else if ( request == null || "true".equals( request.getAttribute( Attribute.PREVIEW_ENABLED ) ) )
        {
            mode = "preview";
        }

        addElement( "render-mode", mode, contextElem );

        // Device context
        if ( context.getDeviceClass() != null )
        {
            addElement( "device-class", context.getDeviceClass(), contextElem );
        }

        // Locale context
        if ( context.getLocale() != null )
        {
            addElement( "locale", context.getLocale().toString(), contextElem );
        }

        // User context
        if ( context.getUser() != null && !context.getUser().isAnonymous() )
        {
            contextElem.addContent( userContextXmlCreator.createUserElement( context.getUser() ) );
        }

        // Site context
        if ( site != null )
        {
            SiteContextXmlCreator siteContextXmlCreator = new SiteContextXmlCreator();
            contextElem.addContent( siteContextXmlCreator.createSiteElement( site, siteProperties ) );
        }

        // Resource context
        if ( context.getMenuItem() != null || context.getPageTemplate() != null )
        {
            ResourceContextXmlCreator contentContextXmlCreator = new ResourceContextXmlCreator( context );
            contextElem.addContent( contentContextXmlCreator.createResourceElement() );
        }

        // Datasource call from a page template
        if ( DataSourceType.PAGETEMPLATE.equals( context.getDataSourceType() ) )
        {
            final Element pageEl;
            if ( PageRequestType.CONTENT.equals( context.getPageRequestType() ) )
            {
                pageEl = pageContextXmlCreator.createPageElementForContentRequest( context.getRegions(), context.getPageTemplate() );
                contextElem.addContent( pageEl );
            }
            else if ( context.getMenuItem() != null )
            {
                pageEl = pageContextXmlCreator.createPageElementForMenuItemRequest( context.getRegions(), context.getMenuItem(),
                                                                                    context.getPageTemplate() );
                contextElem.addContent( pageEl );
            }
        }
        // Datasource call from a portlet
        else if ( context.getDataSourceType().equals( DataSourceType.PORTLET ) && context.getWindow() != null )
        {
            Element portletDocumentEl = null;
            final Document portletDocument = context.getPortletDocument();
            if ( portletDocument != null )
            {
                portletDocumentEl = (Element) portletDocument.getRootElement().detach();
            }
            final Element windowEl =
                windowContextXmlCreator.createPortletWindowElement( context.getWindow(), context.isPortletWindowRenderedInline(),
                                                                    portletDocumentEl );
            contextElem.addContent( windowEl );
        }

        // Profile context
        if ( context.getProfile() != null )
        {
            addElement( "profile", context.getProfile(), contextElem );
        }

        // Styles context
        if ( context.hasCssKeys() )
        {
            Element frameworkEl = getOrCreateElement( contextElem, "framework" );
            Element stylesEl = stylesContextXmlCreator.createStylesElement( context.getCssKeys() );
            frameworkEl.addContent( stylesEl );
        }

        return contextElem;
    }

    private void addElement( String name, String textContent, Element parent )
    {
        parent.addContent( new Element( name ).setText( textContent ) );
    }

    private Element getOrCreateElement( Element parent, String name )
    {
        Element elem = parent.getChild( name );
        if ( elem != null )
        {
            return elem;
        }

        elem = new Element( name );
        parent.addContent( elem );
        return elem;
    }
}
