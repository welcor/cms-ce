package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class IsCaptchaEnabledFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new IsCaptchaEnabledFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = Mockito.mock( PortalFunctionsMediator.class );
        Mockito.when( functions.isCaptchaEnabled( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( true );

        processTemplate( functions, "isCaptchaEnabled" );

        Mockito.verify( functions, Mockito.times( 1 ) ).isCaptchaEnabled( "handler", "operation" );
    }
}
