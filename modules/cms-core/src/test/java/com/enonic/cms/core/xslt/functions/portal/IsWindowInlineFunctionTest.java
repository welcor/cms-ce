/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class IsWindowInlineFunctionTest
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
        Mockito.when( this.mediator.isWindowInline() ).thenReturn( true );

        processTemplate( "isWindowInline" );

        Mockito.verify( this.mediator, Mockito.times( 1 ) ).isWindowInline();
    }
}
