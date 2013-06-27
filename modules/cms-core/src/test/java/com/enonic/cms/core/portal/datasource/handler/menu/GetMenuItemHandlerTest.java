/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.menu;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetMenuItemHandlerTest
    extends AbstractDataSourceHandlerTest<GetMenuItemHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetMenuItemHandlerTest()
    {
        super( GetMenuItemHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.dummyDoc = XMLDocumentFactory.create( "<dummy/>" );
        this.dataSourceService = Mockito.mock( DataSourceService.class );
        this.handler.setDataSourceService( this.dataSourceService );
    }

    @Test
    public void testHandler_get_menu_item()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "3" );
        this.request.addParam( "withParents", "true" );

        Mockito.when( this.dataSourceService.getMenuItem( this.request, 3, true ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuItem( this.request, 3, true );
    }

    @Test
    public void testHandler_default_values()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "3" );

        Mockito.when( this.dataSourceService.getMenuItem( this.request, 3, false ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuItem( this.request, 3, false );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "abc" );
        this.request.addParam( "withParents", "false" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        this.request.addParam( "withParents", "false" );

        this.handler.handle( this.request );
    }
}
