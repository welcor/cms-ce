/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.resolver.locale.LocaleResolverException;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessor;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessorFactory;

public abstract class AbstractXsltScriptResolver
    implements ScriptResolverService
{
    protected final static String RESOLVING_EXCEPTION_MSG = "Failed to resolve value";

    private ResolverInputXMLCreator resolverInputXMLCreator;

    private PortalXsltProcessorFactory xsltProcessorFactory;

    public ScriptResolverResult resolveValue( ResolverContext context, ResourceFile localeResolverScript )
    {
        XMLDocument resolverInput = getResolverInput( context );

        String resolvedValue;
        try
        {
            resolvedValue = resolveWithXsltScript( localeResolverScript, resolverInput );
        }
        catch ( XsltProcessorException e )
        {
            throw new LocaleResolverException( RESOLVING_EXCEPTION_MSG + " using script : " + localeResolverScript.getPath(), e );
        }

        return populateScriptResolverResult( resolvedValue );
    }

    protected abstract ScriptResolverResult populateScriptResolverResult( String resolvedValue );

    protected String cleanWhitespaces( String value )
    {
        value = value.replaceAll( "(\\n)", "" );
        value = value.replaceAll( "(\\t)", "" );
        value = value.replaceAll( "(\\s)", "" );
        return value;
    }

    private String resolveWithXsltScript( ResourceFile xsl, XMLDocument xml )
        throws XsltProcessorException
    {
        final PortalXsltProcessor processor = this.xsltProcessorFactory.createProcessor( new FileResourceName( xsl.getPath() ) );
        processor.setOmitXmlDecl( true );
        String result = processor.process( xml.getAsJDOMSource() );
        return cleanWhitespaces( result );
    }

    protected XMLDocument getResolverInput( ResolverContext context )
    {
        return resolverInputXMLCreator.buildResolverInputXML( context );
    }

    @Autowired
    public void setResolverInputXMLCreator( ResolverInputXMLCreator resolverInputXMLCreator )
    {
        this.resolverInputXMLCreator = resolverInputXMLCreator;
    }

    @Autowired
    public void setXsltProcessorFactory( final PortalXsltProcessorFactory xsltProcessorFactory )
    {
        this.xsltProcessorFactory = xsltProcessorFactory;
    }
}
