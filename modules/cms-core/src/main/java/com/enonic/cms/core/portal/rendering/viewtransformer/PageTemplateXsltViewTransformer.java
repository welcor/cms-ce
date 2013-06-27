/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering.viewtransformer;

import org.jdom.Document;
import org.jdom.transform.JDOMSource;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.PortalRenderingException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.portal.PortalXsltProcessor;

@Component
public class PageTemplateXsltViewTransformer
    extends AbstractXsltViewTransformer
{
    public ViewTransformationResult transform( ResourceFile xsltTemplateFile, Document xml, TransformationParams transformationParams )
    {
        try
        {
            final PortalXsltProcessor processor = createProcessor( xsltTemplateFile.getResourceKey() );
            setParameters( processor, transformationParams );

            String content = processor.process( new JDOMSource( xml ) );

            ViewTransformationResult result = new ViewTransformationResult();
            result.setHttpContentType( processor.getContentType() );
            result.setContent( content );
            result.setOutputMediaType( processor.getOutputMediaType() );
            result.setOutputEncoding( processor.getOutputEncoding() );
            result.setOutputMethod( processor.getOutputMethod() );
            return result;
        }
        catch ( XsltProcessorException e )
        {
            throw new PortalRenderingException( "Failed to transform page template view", e );
        }
    }
}
