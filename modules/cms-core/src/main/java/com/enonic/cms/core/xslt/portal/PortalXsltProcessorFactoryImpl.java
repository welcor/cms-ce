/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.portal;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.FileResourceService;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.base.SaxonXsltProcessorFactory;
import com.enonic.cms.core.xslt.functions.portal.PortalXsltFunctionLibrary;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

@Component
public final class PortalXsltProcessorFactoryImpl
    extends SaxonXsltProcessorFactory
    implements PortalXsltProcessorFactory, InitializingBean
{
    private XsltResourceLoader resourceLoader;

    private XsltTemplatesCache templatesCache;

    private FileResourceService resourceService;

    private CacheFacade cacheFacade;

    private long checkInterval = 5000;

    @Autowired
    public void setPortalFunctions( final PortalFunctionsMediator portalFunctions )
    {
        addFunctionLibrary( new PortalXsltFunctionLibrary( portalFunctions ) );
    }

    @Override
    public PortalXsltProcessor createProcessor( final FileResourceName name )
        throws XsltProcessorException
    {
        final XsltTrackingUriResolver uriResolver = new XsltTrackingUriResolver( this.resourceLoader );
        final XsltTemplatesCacheEntry templates = compileTemplates( name, uriResolver );
        final Transformer transformer = createTransformer( templates, uriResolver );
        return new PortalXsltProcessorImpl( transformer );
    }

    private XsltTemplatesCacheEntry compileTemplates( final FileResourceName name, final XsltTrackingUriResolver resolver )
        throws XsltProcessorException
    {
        XsltTemplatesCacheEntry entry = this.templatesCache.get( name );
        if ( entry != null )
        {
            return entry;
        }

        final Source xsl = loadResource( name );
        final Templates templates = compileTemplate( xsl, resolver );

        entry = new XsltTemplatesCacheEntry( name, templates );
        entry.addIncludes( resolver.getIncludes() );
        this.templatesCache.put( entry );

        return entry;
    }

    private Source loadResource( final FileResourceName name )
        throws XsltProcessorException
    {
        try
        {
            return this.resourceLoader.load( name );
        }
        catch ( final TransformerException e )
        {
            throw new XsltProcessorException( e );
        }
    }

    @Autowired
    public void setResourceService( final FileResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setCacheManager( final CacheManager cacheManager )
    {
        this.cacheFacade = cacheManager.getXsltCache();
    }

    @Value("${cms.cache.xslt.checkInterval}")
    public void setCheckInterval( final long checkInterval )
    {
        this.checkInterval = checkInterval;
    }

    @Override
    public void afterPropertiesSet()
    {
        this.templatesCache = new XsltTemplatesCache( this.cacheFacade, this.resourceService, this.checkInterval );
        this.resourceLoader = new XsltResourceLoader( this.resourceService );
    }
}
