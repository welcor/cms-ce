package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

public class GetMenuDataHandlerTest
    extends AbstractDataSourceHandlerTest<GetMenuDataHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetMenuDataHandlerTest()
    {
        super( GetMenuDataHandler.class );
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
    public void testHandler_get_menu_data()
        throws Exception
    {
        this.request.addParam( "menuKey", "42" );

        Mockito.when( this.dataSourceService.getMenuData( this.request, 42 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuData( this.request, 42 );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_multiple_menu_items_unsupported()
        throws Exception
    {
        this.request.addParam( "menuKey", "42,43" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "menuKey", "*" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        this.handler.handle( this.request );
    }

}