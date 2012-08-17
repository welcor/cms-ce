package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class IsCaptchaEnabledFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected PortalFunctionsMediator newMediator()
    {
        return Mockito.mock( PortalFunctionsMediator.class );
    }

    @Test
    public void testFunction()
        throws Exception
    {
        Mockito.when( this.mediator.isCaptchaEnabled( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( true );

        processTemplate( "isCaptchaEnabled" );

        Mockito.verify( this.mediator, Mockito.times( 1 ) ).isCaptchaEnabled( "handler", "operation" );
    }
}
