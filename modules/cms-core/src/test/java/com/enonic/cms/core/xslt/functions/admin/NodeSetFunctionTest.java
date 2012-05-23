package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class NodeSetFunctionTest
    extends AbstractAdminFunctionTest
{
    @Override
    protected AbstractAdminFunction newFunction()
    {
        return new NodeSetFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "nodeSet" );
    }
}
