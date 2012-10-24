package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

import static org.mockito.Matchers.eq;

public class GetMyContentByCategoryHandlerTest
    extends AbstractDataSourceHandlerTest<GetMyContentByCategoryHandler>
{
    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetMyContentByCategoryHandlerTest()
    {
        super( GetMyContentByCategoryHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.dummyDoc = XMLDocumentFactory.create( "<dummy/>" );
        this.dataSourceService = Mockito.mock( DataSourceService.class );
        this.handler.setDataSourceService( this.dataSourceService );

        this.request.addParam( "query", "title STARTS WITH 'Content '" );
        this.request.addParam( "categoryKeys", "10,20,30" );
        this.request.addParam( "recursive", "true" );
        this.request.addParam( "orderBy", "@title asc" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "titlesOnly", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );
        this.request.addParam( "parentChildrenLevel", "8" );
        this.request.addParam( "relatedTitlesOnly", "true" );
        this.request.addParam( "includeTotalCount", "true" );
        this.request.addParam( "includeUserRights", "true" );
        this.request.addParam( "contentTypeKeys", "100,200,300" );
    }

    @Test
    public void testHandler_get_mycontent_by_category()
        throws Exception
    {
        Mockito.when(
            this.dataSourceService.getMyContentByCategory(
                eq( request ),
                eq( "title STARTS WITH 'Content '" ),
                eq( new int[]{10, 20, 30} ),
                eq( true ),
                eq( "@title asc" ),
                eq( 0 ),
                eq( 10 ),
                eq( true ),
                eq( 1 ),
                eq( 0 ),
                eq( 8 ),
                eq( true ),
                eq( true ),
                eq( true ),
                eq( new int[]{100, 200, 300} ) ) )
            .thenReturn( this.dummyDoc );

        this.handler.handle( this.request );

        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMyContentByCategory(
            eq( request ),
            eq( "title STARTS WITH 'Content '" ),
            eq( new int[]{10, 20, 30} ),
            eq( true ),
            eq( "@title asc" ),
            eq( 0 ),
            eq( 10 ),
            eq( true ),
            eq( 1 ),
            eq( 0 ),
            eq( 8 ),
            eq( true ),
            eq( true ),
            eq( true ),
            eq( new int[]{100, 200, 300} ) );
    }
}
