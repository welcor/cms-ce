package com.enonic.cms.core.xslt.functions.portal;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionTest;
import com.enonic.cms.core.xslt.lib.MockPortalFunctionsMediator;
import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public abstract class AbstractPortalFunctionTest
    extends AbstractXsltFunctionTest<PortalXsltFunctionLibrary>
{
    protected PortalFunctionsMediator mediator;

    protected PortalFunctionsMediator newMediator()
    {
        return new MockPortalFunctionsMediator();
    }

    @Override
    protected PortalXsltFunctionLibrary newFunctionLibrary()
    {
        this.mediator = newMediator();
        return new PortalXsltFunctionLibrary( this.mediator );
    }
}
