package com.enonic.cms.core.portal.datasource.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetContentBySectionHandlerTest
    extends AbstractDataSourceHandlerTest<GetContentBySectionHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetContentBySectionHandlerTest()
    {
        super( GetContentBySectionHandler.class );
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
    public void testHandler_content_by_section()
        throws Exception
    {
        this.request.addParam( "menuItemKeys", "11" );
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when( this.dataSourceService.getContentBySection( this.request, new int[]{11}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getContentBySection( this.request, new int[]{11}, 1, "", "", 0, 10,
                                                                                          true, 1, 0 );
    }

    @Test
    public void testHandler_content_by_multiple_sections()
        throws Exception
    {
        this.request.addParam( "menuItemKeys", "11,3" );
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when(
            this.dataSourceService.getContentBySection( this.request, new int[]{11, 3}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getContentBySection( this.request, new int[]{11, 3}, 1, "", "", 0, 10,
                                                                                          true, 1, 0 );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "menuItemKeys", "mymenu" );
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "33" );
        this.request.addParam( "childrenLevel", "true" );
        this.request.addParam( "parentLevel", "false" );

        Mockito.when( this.dataSourceService.getContentBySection( this.request, new int[]{11}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_menuItemKeys_parameter()
        throws Exception
    {
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when( this.dataSourceService.getContentBySection( this.request, new int[]{11}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getContentBySection( this.request, new int[]{11}, 1, "", "", 0, 10,
                                                                                          true, 1, 0 );
    }

}
