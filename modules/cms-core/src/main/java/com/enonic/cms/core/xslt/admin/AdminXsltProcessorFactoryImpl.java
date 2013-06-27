/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.admin;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltResource;
import com.enonic.cms.core.xslt.base.SaxonXsltProcessorFactory;
import com.enonic.cms.core.xslt.functions.admin.AdminXsltFunctionLibrary;

@Component
public final class AdminXsltProcessorFactoryImpl
    extends SaxonXsltProcessorFactory
    implements AdminXsltProcessorFactory
{
    public AdminXsltProcessorFactoryImpl()
    {
        addFunctionLibrary( new AdminXsltFunctionLibrary() );
    }

    @Override
    public AdminXsltProcessor createProcessor( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final Templates templates = compileTemplate( xsl, resolver );
        final Transformer transformer = createTransformer( templates, resolver );
        return new AdminXsltProcessorImpl( transformer );
    }

    @Override
    public AdminXsltProcessor createProcessor( final XsltResource xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        return createProcessor( xsl.getAsSource(), resolver );
    }
}
