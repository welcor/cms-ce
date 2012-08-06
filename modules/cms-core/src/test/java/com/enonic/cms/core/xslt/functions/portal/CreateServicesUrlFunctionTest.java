package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class CreateServicesUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new CreateServicesUrlFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "createServicesUrl" );
    }
}
