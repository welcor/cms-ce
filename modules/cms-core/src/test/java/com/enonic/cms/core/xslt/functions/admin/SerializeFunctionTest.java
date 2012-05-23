package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class SerializeFunctionTest
    extends AbstractAdminFunctionTest
{
    @Override
    protected AbstractAdminFunction newFunction()
    {
        return new SerializeFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "serialize" );
    }
}
