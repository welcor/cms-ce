package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class CreateResourceUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new CreateResourceUrlFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "createResourceUrl" );
    }
}
