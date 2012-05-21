package com.enonic.cms.web.portal.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.web.portal.PortalWebContext;

public class WebHandlerRegistryImplTest
{
    private PortalWebContext context;

    private WebHandler matchHandler;

    private WebHandler noMatchHandler;

    @Before
    public void setUp()
    {
        this.context = new PortalWebContext();

        this.matchHandler = Mockito.mock( WebHandler.class );
        Mockito.when( this.matchHandler.canHandle( this.context ) ).thenReturn( true );

        this.noMatchHandler = Mockito.mock( WebHandler.class );
        Mockito.when( this.noMatchHandler.canHandle( this.context ) ).thenReturn( false );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoHandlers()
    {
        WebHandlerRegistryImpl registry = new WebHandlerRegistryImpl();
        registry.setHandlers();
        registry.find( this.context );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoMatch()
    {
        WebHandlerRegistryImpl registry = new WebHandlerRegistryImpl();
        registry.setHandlers( this.noMatchHandler );
        registry.find( this.context );
    }

    @Test
    public void testMatch()
    {
        WebHandlerRegistryImpl registry = new WebHandlerRegistryImpl();
        registry.setHandlers( this.noMatchHandler, this.matchHandler );
        registry.find( this.context );
    }
}
