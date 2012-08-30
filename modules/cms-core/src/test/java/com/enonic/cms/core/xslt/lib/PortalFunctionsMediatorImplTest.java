package com.enonic.cms.core.xslt.lib;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctions;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsContext;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsFactory;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.WindowKey;

import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PortalFunctionsMediatorImplTest
{
    private PortalFunctionsMediatorImpl portalFunctionsMediator = new PortalFunctionsMediatorImpl();

    private PortalFunctions portalFunctions;

    private PortalFunctionsFactory portalFunctionsFactory;

    @Before
    public void setUp()
    {
        portalFunctionsFactory = mock( PortalFunctionsFactory.class );
        portalFunctions = mock( PortalFunctions.class );
        PortalFunctionsContext context = mock( PortalFunctionsContext.class );

        when( portalFunctionsFactory.createPortalFunctions() ).thenReturn( portalFunctions );
        when( portalFunctionsFactory.getContext() ).thenReturn( context );

        portalFunctionsMediator.setPortalFunctionsFactory( portalFunctionsFactory );
    }

    @Test
    public void createPageUrlTest()
    {
        portalFunctionsMediator.createPageUrl( null, null );
        portalFunctionsMediator.createPageUrl( null, new String[]{"test", "test1"} );
        portalFunctionsMediator.createPageUrl( "123", new String[]{"test", "test1"} );

        verify( portalFunctions, times( 1 ) ).createPageUrl( null );
        verify( portalFunctions, times( 1 ) ).createPageUrl( isA( String[].class ) );
        verify( portalFunctions, times( 1 ) ).createPageUrl( isA( MenuItemKey.class ), isA( String[].class ) );
    }

    @Test
    public void createWindowUrlTest()
    {
        portalFunctionsMediator.createWindowUrl( null, null, null );
        portalFunctionsMediator.createWindowUrl( null, new String[]{"test", "test1"}, null );
        portalFunctionsMediator.createWindowUrl( "123:11", new String[]{"test", "test1"}, null );
        portalFunctionsMediator.createWindowUrl( "123:11", new String[]{"test", "test1"}, "format" );

        verify( portalFunctions, times( 1 ) ).createWindowUrl();
        verify( portalFunctions, times( 1 ) ).createWindowUrl( isA( String[].class ) );
        verify( portalFunctions, times( 1 ) ).createWindowUrl( isA( WindowKey.class ), isA( String[].class ) );
        verify( portalFunctions, times( 1 ) ).createWindowUrl( isA( WindowKey.class ), isA( String[].class ), isA( String.class ) );
    }


    @Test
    public void localizeTest()
    {
        portalFunctionsMediator.localize( "phrase", null, null );
        portalFunctionsMediator.localize( "phrase", new String[]{"test1", "test2"}, null );
        portalFunctionsMediator.localize( "phrase", null, "locale" );
        portalFunctionsMediator.localize( "phrase", new String[]{"test1", "test2"}, "locale" );

        verify( portalFunctions, times( 1 ) ).localize( "phrase" );
        verify( portalFunctions, times( 1 ) ).localize( "phrase", new String[]{"test1", "test2"} );
        verify( portalFunctions, times( 1 ) ).localize( "phrase", null, "locale" );
        verify( portalFunctions, times( 1 ) ).localize( "phrase", new String[]{"test1", "test2"}, "locale" );
    }


}
