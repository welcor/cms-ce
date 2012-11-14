package com.enonic.cms.core.portal.datasource.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;

import static org.mockito.Matchers.eq;

public class GetContentHandlerTest
    extends AbstractDataSourceHandlerTest<GetContentHandler>
{
    public GetContentHandlerTest()
    {
        super( GetContentHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.handler.setDataSourceService( this.dataSourceService );

        this.request.addParam( "contentKeys", "10,20,30" );
        this.request.addParam( "query", "title STARTS WITH 'Content '" );
        this.request.addParam( "orderBy", "@title asc" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );
        this.request.addParam( "facets", "" );
    }

    @Test
    public void testHandler_get_content_by_query()
        throws Exception
    {
        Mockito.when(
            this.dataSourceService.getContent(
                eq( request ),
                eq( new int[] { 10, 20, 30 } ),
                eq( "title STARTS WITH 'Content '" ),
                eq( "@title asc" ),
                eq( 0 ),
                eq( 10 ),
                eq( true ),
                eq( 1 ),
                eq( 0 ), eq( "" ) ) )
            .thenReturn( this.dummyDoc );

        this.handler.handle( this.request );

        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getContent(
            eq( request ),
            eq( new int[] { 10, 20, 30 } ),
            eq( "title STARTS WITH 'Content '" ),
            eq( "@title asc" ),
            eq( 0 ),
            eq( 10 ),
            eq( true ),
            eq( 1 ),
            eq( 0 ), eq( "" ) );
    }
}
