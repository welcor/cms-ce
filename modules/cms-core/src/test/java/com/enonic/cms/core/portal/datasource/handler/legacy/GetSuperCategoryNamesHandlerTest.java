package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetSuperCategoryNamesHandlerTest
    extends AbstractDataSourceHandlerTest<GetSuperCategoryNamesHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetSuperCategoryNamesHandlerTest()
    {
        super( GetSuperCategoryNamesHandler.class );
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
    public void testHandler_super_category_names()
        throws Exception
    {
        this.request.addParam( "categoryKey", "11" );
        this.request.addParam( "includeContentCount", "true" );
        this.request.addParam( "includeCurrent", "true" );

        Mockito.when( this.dataSourceService.getSuperCategoryNames( this.request, 11, true, true ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getSuperCategoryNames( this.request, 11, true, true );
    }

    @Test
    public void testHandler_default_parameter_values()
        throws Exception
    {
        this.request.addParam( "categoryKey", "12" );

        Mockito.when( this.dataSourceService.getSuperCategoryNames( this.request, 12, false, false ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getSuperCategoryNames( this.request, 12, false, false );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        this.request.addParam( "includeContentCount", "true" );
        this.request.addParam( "includeCurrent", "true" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "categoryKey", "33,44" );
        this.request.addParam( "includeContentCount", "true" );
        this.request.addParam( "includeCurrent", "true" );

        this.handler.handle( this.request );
    }
}
