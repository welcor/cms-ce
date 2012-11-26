package com.enonic.cms.core.portal.datasource.handler.menu;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetMenuHandlerTest
    extends AbstractDataSourceHandlerTest<GetMenuHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetMenuHandlerTest()
    {
        super( GetMenuHandler.class );
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
    public void testHandler_get_menu()
        throws Exception
    {
        this.request.addParam( "siteKey", "3" );
        this.request.addParam( "tagItem", "42" );
        this.request.addParam( "levels", "0" );

        Mockito.when( this.dataSourceService.getMenu( this.request, 3, 42, 0 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenu( this.request, 3, 42, 0 );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "siteKey", "3" );
        this.request.addParam( "tagItem", "42" );
        this.request.addParam( "levels", "false" ); // should be a number

        this.handler.handle( this.request );
    }

}