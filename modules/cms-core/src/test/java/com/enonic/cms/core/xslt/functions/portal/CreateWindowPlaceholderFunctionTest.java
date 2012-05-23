package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

public class CreateWindowPlaceholderFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new CreateWindowPlaceholderFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "createWindowPlaceholder" );
    }
}
