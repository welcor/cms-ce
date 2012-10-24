package com.enonic.cms.core.portal.datasource2.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

public class GetRelatedContentHandlerTest
    extends AbstractDataSourceHandlerTest<GetRelatedContentHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetRelatedContentHandlerTest()
    {
        super( GetRelatedContentHandler.class );
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
    public void testHandler_related_content()
        throws Exception
    {
        this.request.addParam( "contentKeys", "11" );
        this.request.addParam( "relation", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when( this.dataSourceService.getRelatedContent( this.request, new int[]{11}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getRelatedContent( this.request, new int[]{11}, 1, "", "", 0, 10, true,
                                                                                        1, 0 );
    }


    @Test
    public void testHandler_multiple_contents()
        throws Exception
    {
        this.request.addParam( "contentKeys", "11,3,4" );
        this.request.addParam( "relation", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when(
            this.dataSourceService.getRelatedContent( this.request, new int[]{11, 3, 4}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getRelatedContent( this.request, new int[]{11, 3, 4}, 1, "", "", 0, 10,
                                                                                        true, 1, 0 );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "contentKeys", "11;44" );
        this.request.addParam( "relation", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when(
            this.dataSourceService.getRelatedContent( this.request, new int[]{11, 44}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        // param "contentKeys" not set
        this.request.addParam( "relation", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        Mockito.when( this.dataSourceService.getRelatedContent( this.request, new int[]{}, 1, "", "", 0, 10, true, 1, 0 ) ).thenReturn(
            this.dummyDoc );
        this.handler.handle( this.request );
    }
}
