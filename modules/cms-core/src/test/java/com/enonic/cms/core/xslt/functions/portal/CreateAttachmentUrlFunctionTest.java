package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

public class CreateAttachmentUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new CreateAttachmentUrlFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctions();
        processTemplate( functions, "createAttachmentUrl" );
    }
}
