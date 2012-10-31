package com.enonic.cms.core.xslt.admin;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltResource;

public interface AdminXsltProcessorFactory
{
    public AdminXsltProcessor createProcessor( Source xsl, URIResolver resolver )
        throws XsltProcessorException;

    public AdminXsltProcessor createProcessor( XsltResource xsl, URIResolver resolver )
        throws XsltProcessorException;
}
