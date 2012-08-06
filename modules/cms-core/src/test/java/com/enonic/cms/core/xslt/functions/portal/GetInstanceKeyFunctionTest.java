package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class GetInstanceKeyFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new GetInstanceKeyFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "getInstanceKey" );
    }
}
