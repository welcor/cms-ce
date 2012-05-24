package com.enonic.cms.core.xslt.functions.admin;

import com.enonic.cms.core.xslt.functions.XsltFunctionLibrary;

public final class AdminXsltFunctionLibrary
    extends XsltFunctionLibrary
{
    public AdminXsltFunctionLibrary()
    {
        add( new EvaluateFunction() );
        add( new NodeSetFunction() );
        add( new SerializeFunction() );
        add( new UniqueIdFunction() );
        add( new UrlEncodeFunction() );
    }
}
