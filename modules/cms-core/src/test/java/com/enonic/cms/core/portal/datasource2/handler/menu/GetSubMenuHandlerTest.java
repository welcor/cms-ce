package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

public class GetSubMenuHandlerTest
    extends AbstractDataSourceHandlerTest<GetSubMenuHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetSubMenuHandlerTest()
    {
        super( GetSubMenuHandler.class );
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
    public void testHandler_get_submenu()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "3" );
        this.request.addParam( "tagItem", "42" );
        this.request.addParam( "levels", "1" );

        Mockito.when( this.dataSourceService.getSubMenu( this.request, 3, 42, 1 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getSubMenu( this.request, 3, 42, 1 );
    }

    @Test
    public void testHandler_default_values()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "42" );

        Mockito.when( this.dataSourceService.getSubMenu( this.request, 42, -1, 0 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getSubMenu( this.request, 42, -1, 0 );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "3,44" );
        this.request.addParam( "tagItem", "42" );
        this.request.addParam( "levels", "0" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        // parameter "menuItemKey" not set
        this.request.addParam( "tagItem", "42" );
        this.request.addParam( "levels", "0" );

        this.handler.handle( this.request );
    }

}