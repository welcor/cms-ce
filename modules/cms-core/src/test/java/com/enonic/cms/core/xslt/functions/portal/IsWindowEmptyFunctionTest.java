package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;
import org.mockito.Mockito;

public class IsWindowEmptyFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new IsWindowEmptyFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = Mockito.mock( PortalFunctionsMediator.class );
        Mockito.when( functions.isWindowEmpty( Mockito.anyString(), Mockito.any( String[].class ) ) ).thenReturn( true );

        processTemplate( functions, "isWindowEmpty" );

        Mockito.verify( functions, Mockito.times( 2 ) ).isWindowEmpty( "window-key", new String[0] );
        Mockito.verify( functions, Mockito.times( 1 ) ).isWindowEmpty( "window-key", new String[]{"a", "3"} );
    }
}
