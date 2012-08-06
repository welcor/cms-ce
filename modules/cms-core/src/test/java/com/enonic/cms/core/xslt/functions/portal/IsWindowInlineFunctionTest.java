package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class IsWindowInlineFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new IsWindowInlineFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = Mockito.mock( PortalFunctionsMediator.class );
        Mockito.when( functions.isWindowInline() ).thenReturn( true );

        processTemplate( functions, "isWindowInline" );

        Mockito.verify( functions, Mockito.times( 1 ) ).isWindowInline();
    }
}
