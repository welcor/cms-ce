/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering.viewtransformer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.TemplateParameterType;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessor;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessorFactory;

public abstract class AbstractXsltViewTransformer
{
    protected final static String XSLT_NS = "http://www.w3.org/1999/XSL/Transform";

    protected ResourceService resourceService;

    private PortalXsltProcessorFactory xsltProcessorFactory;

    protected final PortalXsltProcessor createProcessor( final ResourceKey xsl )
        throws XsltProcessorException
    {
        return this.xsltProcessorFactory.createProcessor( new FileResourceName( xsl.toString() ) );
    }

    protected final PortalXsltProcessor createProcessor( final ResourceKey xsl, final boolean omitXmlDecl )
        throws XsltProcessorException
    {
        final PortalXsltProcessor processor = createProcessor( xsl );
        processor.setOmitXmlDecl( omitXmlDecl );
        return processor;
    }

    protected final void setParameters( final PortalXsltProcessor processor, final TransformationParams params )
    {
        final Map<String, String> map = processor.getCustomParameterTypes();

        for ( final Map.Entry<String, String> entry : map.entrySet() )
        {
            final String parameterName = entry.getKey();
            final TemplateParameterType parameterType = TemplateParameterType.parse( entry.getValue() );
            final TransformationParameter parameter = params.get( parameterName );
            Object parameterValue = parameter != null ? parameter.getValue() : null;

            if ( ( parameterValue == null ) && ( parameterType != null ) )
            {
                parameterValue = "";
            }

            processor.setParameter( parameterName, parameterValue );
        }
    }

    @Autowired
    public final void setResourceService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setXsltProcessorFactory( final PortalXsltProcessorFactory xsltProcessorFactory )
    {
        this.xsltProcessorFactory = xsltProcessorFactory;
    }
}
