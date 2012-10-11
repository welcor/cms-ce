/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.saxon;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.XsltResource;
import com.enonic.cms.core.xslt.cache.TemplatesXsltCache;
import com.enonic.cms.core.xslt.functions.admin.AdminXsltFunctionLibrary;
import com.enonic.cms.core.xslt.functions.portal.PortalXsltFunctionLibrary;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

/**
 * This class implements the standard xslt processor manager.
 */
@Component
public final class SaxonProcessorManager
    implements XsltProcessorManager
{
    private final TransformerFactoryImpl transformerFactory;

    private final Configuration configuration;

    private TemplatesXsltCache cache;

    public SaxonProcessorManager()
        throws Exception
    {
        XsltProcessorManagerAccessor.setProcessorManager( this );

        this.transformerFactory = new TransformerFactoryImpl();

        this.configuration = this.transformerFactory.getConfiguration();
        this.configuration.setLineNumbering( true );
        this.configuration.setHostLanguage( Configuration.XSLT );
        this.configuration.setVersionWarning( false );
        this.configuration.setCompileWithTracing( true );
        this.configuration.setValidationWarnings( true );

        new AdminXsltFunctionLibrary().register( this.configuration );
    }

    public XsltProcessor createProcessor( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        return new XsltProcessorImpl( createTransformer( xsl, resolver ) );
    }

    public XsltProcessor createProcessor( final XsltResource xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        return createProcessor( xsl.getAsSource(), resolver );
    }

    @Autowired
    public void setTemplatesXsltCache( final TemplatesXsltCache cache )
    {
        this.cache = cache;
    }

    @Autowired
    public void setPortalFunctions( final PortalFunctionsMediator portalFunctions )
    {
        new PortalXsltFunctionLibrary( portalFunctions ).register( this.configuration );
    }

    private Transformer createTransformer( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final Templates templates = createTemplates( xsl, resolver );
        return createTransformer( templates, resolver );
    }


    private Templates createTemplates( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();

        try
        {
            this.transformerFactory.setErrorListener( errors );
            this.transformerFactory.setURIResolver( resolver );
            return this.transformerFactory.newTemplates( xsl );
        }
        catch ( final Exception e )
        {

            throw new XsltProcessorException( e, errors );
        }
    }

    private Transformer createTransformer( final Templates templates, final URIResolver resolver )
        throws XsltProcessorException
    {
        try
        {
            final Transformer transformer = templates.newTransformer();
            transformer.setURIResolver( resolver );
            return transformer;
        }
        catch ( final Exception e )
        {
            throw new XsltProcessorException( e );
        }
    }

    @Override
    public XsltProcessor createCachedProcessor( final XsltResource xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        if ( this.cache == null )
        {
            return createProcessor( xsl, resolver );
        }

        Templates templates = this.cache.get( xsl );
        if ( templates == null )
        {
            templates = createTemplates( xsl.getAsSource(), resolver );
            this.cache.put( xsl, templates );
        }

        return new XsltProcessorImpl( createTransformer( templates, resolver ) );
    }
}
