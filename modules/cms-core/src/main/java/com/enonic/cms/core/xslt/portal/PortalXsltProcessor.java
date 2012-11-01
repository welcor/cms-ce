package com.enonic.cms.core.xslt.portal;

import java.util.Map;

import com.enonic.cms.core.xslt.XsltProcessor;

public interface PortalXsltProcessor
    extends XsltProcessor
{
    public Map<String, String> getCustomParameterTypes();
}
