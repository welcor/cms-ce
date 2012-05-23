package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

public class EvaluateFunctionTest
    extends AbstractAdminFunctionTest
{
    @Override
    protected AbstractAdminFunction newFunction()
    {
        return new EvaluateFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "evaluate" );
    }
}
