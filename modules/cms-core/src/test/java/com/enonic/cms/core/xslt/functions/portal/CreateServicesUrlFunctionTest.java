/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.MockPortalFunctionsMediator;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class CreateServicesUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "createServicesUrl" );
    }
}
