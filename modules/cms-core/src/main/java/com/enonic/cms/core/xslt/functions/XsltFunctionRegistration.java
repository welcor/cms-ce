package com.enonic.cms.core.xslt.functions;

import net.sf.saxon.Configuration;

// Use IntegratedFunctionLibrary instead
public interface XsltFunctionRegistration
{
    public void register( Configuration config );
}
