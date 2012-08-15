package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;

import com.enonic.cms.core.xslt.lib.MockPortalFunctionsMediator;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class ParseDocumentFunctionTest
    extends AbstractPortalFunctionTest
{

    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new ParseDocumentFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = new MockPortalFunctionsMediator();
        processTemplate( functions, "parseDocument" );

    }

}

