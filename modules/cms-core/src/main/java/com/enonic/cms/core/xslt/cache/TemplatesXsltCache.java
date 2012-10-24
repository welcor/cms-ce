package com.enonic.cms.core.xslt.cache;

import javax.xml.transform.Templates;

import com.enonic.cms.core.xslt.XsltResource;

public interface TemplatesXsltCache
{
    public Templates get( XsltResource xsl );

    public void put( XsltResource xsl, Templates templates );

    public void clear();
}
