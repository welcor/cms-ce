package com.enonic.cms.core.xslt.functions.admin;

import com.enonic.cms.core.xslt.functions.XsltFunctionLibrary;

public final class AdminXsltFunctionLibrary
    extends XsltFunctionLibrary
{
    public AdminXsltFunctionLibrary()
    {
        this( new UniqueIdGeneratorImpl() );
    }

    public AdminXsltFunctionLibrary( final UniqueIdGenerator generator )
    {
        add( new SerializeFunction() );
        add( new UniqueIdFunction( generator ) );
        add( new UrlEncodeFunction() );
        add( new ToIntegerFunction() );
    }
}
