package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

public class Md5DigestFunctionTest
    extends AbstractPortalFunctionTest
{
    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "md5Digest" );
    }
}
