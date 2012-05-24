package com.enonic.cms.core.xslt.cache;

import javax.xml.transform.Templates;

import net.sf.saxon.s9api.XsltExecutable;

import com.enonic.cms.core.xslt.XsltResource;

public interface TemplatesXsltCache
{
    public XsltExecutable get( XsltResource xsl );

    public void put( XsltResource xsl, XsltExecutable templates );

    public void clear();
}
