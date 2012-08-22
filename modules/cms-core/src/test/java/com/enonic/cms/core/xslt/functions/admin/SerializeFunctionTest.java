package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class SerializeFunctionTest
    extends AbstractAdminFunctionTest
{
    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "serialize" );
    }
}
