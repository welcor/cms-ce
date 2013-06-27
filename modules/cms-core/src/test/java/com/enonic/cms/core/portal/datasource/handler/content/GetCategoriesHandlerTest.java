/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetCategoriesHandlerTest
    extends AbstractDataSourceHandlerTest<GetCategoriesHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetCategoriesHandlerTest()
    {
        super( GetCategoriesHandler.class );
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
    public void testHandler_categories()
        throws Exception
    {
        this.request.addParam( "categoryKey", "11" );
        this.request.addParam( "levels", "2" );
        this.request.addParam( "includeContentCount", "false" );
        this.request.addParam( "includeTopCategory", "true" );

        Mockito.when( this.dataSourceService.getCategories( this.request, 11, 2, false, true ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getCategories( this.request, 11, 2, false, true );
    }

    @Test
    public void testHandler_default_parameter_values()
        throws Exception
    {
        this.request.addParam( "categoryKey", "12" );

        Mockito.when( this.dataSourceService.getCategories( this.request, 12, 0, false, true ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getCategories( this.request, 12, 0, false, true );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        this.request.addParam( "levels", "0" );
        this.request.addParam( "includeContentCount", "false" );
        this.request.addParam( "includeTopCategory", "true" );

        Mockito.when( this.dataSourceService.getCategories( this.request, 11, 0, false, true ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "categoryKey", "CAT" );
        this.request.addParam( "levels", "0" );
        this.request.addParam( "includeContentCount", "false" );
        this.request.addParam( "includeTopCategory", "true" );

        this.handler.handle( this.request );
    }
}
