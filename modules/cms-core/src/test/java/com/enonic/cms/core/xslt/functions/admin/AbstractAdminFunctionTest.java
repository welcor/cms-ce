package com.enonic.cms.core.xslt.functions.admin;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionTest;

public abstract class AbstractAdminFunctionTest
    extends AbstractXsltFunctionTest<AdminXsltFunctionLibrary>
{
    @Override
    protected AdminXsltFunctionLibrary newFunctionLibrary()
    {
        return new AdminXsltFunctionLibrary();
    }
}
