package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class UrlEncodeFunctionTest
    extends AbstractAdminFunctionTest
{
    @Override
    protected AbstractAdminFunction newFunction()
    {
        return new UrlEncodeFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "urlEncode" );
    }
}
