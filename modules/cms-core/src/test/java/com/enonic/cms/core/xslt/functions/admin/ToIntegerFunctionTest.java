package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class ToIntegerFunctionTest
    extends AbstractAdminFunctionTest
{

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "toInteger" );
    }


}
