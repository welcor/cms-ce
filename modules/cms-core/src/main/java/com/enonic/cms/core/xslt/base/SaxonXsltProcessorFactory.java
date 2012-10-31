package com.enonic.cms.core.xslt.base;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.cms.core.xslt.functions.XsltFunctionLibrary;

public abstract class SaxonXsltProcessorFactory
    extends BaseXsltProcessorFactory
{
    private final Configuration configuration;

    public SaxonXsltProcessorFactory()
    {
        this.configuration = new Configuration();
        this.configuration.setLineNumbering( true );
        this.configuration.setHostLanguage( Configuration.XSLT );
        this.configuration.setVersionWarning( false );
        this.configuration.setCompileWithTracing( true );
        this.configuration.setValidationWarnings( true );
    }

    protected final void addFunctionLibrary( final XsltFunctionLibrary library )
    {
        library.register( this.configuration );
    }

    @Override
    protected final TransformerFactory createTransformerFactory()
    {
        return new TransformerFactoryImpl( this.configuration );
    }
}
