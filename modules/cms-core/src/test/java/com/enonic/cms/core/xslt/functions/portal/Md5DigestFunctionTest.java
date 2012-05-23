package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

public class Md5DigestFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new Md5DigestFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "md5Digest" );
    }
}
