package com.enonic.cms.core.xslt.functions.portal;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class ImageExistsFunctionTest
    extends AbstractPortalFunctionTest
{
    @Override
    protected AbstractPortalFunction newFunction()
    {
        return new ImageExistsFunction();
    }

    @Test
    public void testFunction()
        throws Exception
    {
        final PortalFunctionsMediator functions = Mockito.mock( PortalFunctionsMediator.class );
        Mockito.when( functions.imageExists( Mockito.anyString() ) ).thenReturn( true );

        processTemplate( functions, "imageExists" );

        Mockito.verify( functions, Mockito.times( 1 ) ).imageExists( "image-key" );
    }
}
