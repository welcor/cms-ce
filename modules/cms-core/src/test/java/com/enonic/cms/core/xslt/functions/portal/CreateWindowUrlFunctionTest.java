package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

public class CreateWindowUrlFunctionTest
    extends AbstractPortalFunctionTest
{
    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "createWindowUrl" );
    }
}
