/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class UrlEncodeFunctionTest
    extends AbstractAdminFunctionTest
{
    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "urlEncode" );
    }
}
