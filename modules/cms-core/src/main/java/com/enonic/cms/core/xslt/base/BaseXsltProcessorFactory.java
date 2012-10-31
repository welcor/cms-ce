package com.enonic.cms.core.xslt.base;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

public abstract class BaseXsltProcessorFactory
{
    protected abstract TransformerFactory createTransformerFactory();

    protected final Transformer createTransformer( final Templates templates, final URIResolver resolver )
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

    protected final Templates compileTemplate( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        final TransformerFactory factory = createTransformerFactory();

        try
        {
            factory.setErrorListener( errors );
            factory.setURIResolver( resolver );
            return factory.newTemplates( xsl );
        }
        catch ( final Exception e )
        {
            throw new XsltProcessorException( e, errors );
        }
    }
}
