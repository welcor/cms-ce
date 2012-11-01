/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering.viewtransformer;

import org.jdom.transform.JDOMSource;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.PortletXsltViewTransformationException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessor;

@Component
public class PortletXsltViewTransformer
    extends AbstractXsltViewTransformer
{
    public ViewTransformationResult transform( ResourceFile viewFile, TransformationParams transformationParams, XMLDocument xml )
    {
        try
        {
            final PortalXsltProcessor processor = createProcessor( viewFile.getResourceKey(), true );
            setParameters( processor, transformationParams );

            String stringResult;

            stringResult = processor.process( new JDOMSource( xml.getAsJDOMDocument() ) );
            ViewTransformationResult viewTransformationResult = new ViewTransformationResult();
            viewTransformationResult.setContent( stringResult );
            viewTransformationResult.setHttpContentType( processor.getContentType() );
            viewTransformationResult.setOutputMethod( processor.getOutputMethod() );
            viewTransformationResult.setOutputEncoding( processor.getOutputEncoding() );
            viewTransformationResult.setOutputMediaType( processor.getOutputMediaType() );
            return viewTransformationResult;
        }
        catch ( XsltProcessorException e )
        {
            throw new PortletXsltViewTransformationException( "Failed to transform portlet template view", e );
        }
    }
}
