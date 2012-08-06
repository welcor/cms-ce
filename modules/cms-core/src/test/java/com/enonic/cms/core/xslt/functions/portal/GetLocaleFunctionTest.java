package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class GetLocaleFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new GetLocaleFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "getLocale" );
    }
}
