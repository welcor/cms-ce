package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.MockPortalFunctionsMediator;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class CreateResourceUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "createResourceUrl" );
    }
}
