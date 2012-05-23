package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

public class CreateBinaryUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new CreateBinaryUrlFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "createBinaryUrl" );
    }
}
