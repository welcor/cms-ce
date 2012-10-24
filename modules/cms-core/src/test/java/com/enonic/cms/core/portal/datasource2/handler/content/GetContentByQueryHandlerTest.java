package com.enonic.cms.core.portal.datasource2.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource2.handler.StoreMatcher;
import com.enonic.cms.core.preview.PreviewContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

public class GetContentByQueryHandlerTest
    extends AbstractDataSourceHandlerTest<GetContentByQueryHandler>
{
    public GetContentByQueryHandlerTest()
    {
        super( GetContentByQueryHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.handler.setDataSourceService( this.dataSourceService );

        this.request.addParam( "query", "title STARTS WITH 'Content '" );
        this.request.addParam( "orderBy", "@title asc" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );
    }

    @Test
    public void testHandler_get_content_by_query()
        throws Exception
    {
        Mockito.when(
            this.dataSourceService.getContentByQuery(
                argThat( new StoreMatcher<DataSourceRequest>() {
                    protected void store( DataSourceRequest value ) {
                        assertEquals( request.getUser(), value.getUser() );
                        assertEquals( PreviewContext.NO_PREVIEW, value.getPreviewContext() );
                    }
                } ),
                eq( "title STARTS WITH 'Content '" ),
                eq( "@title asc" ),
                eq( 0 ),
                eq( 10 ),
                eq( true ),
                eq( 1 ),
                eq( 0 ) ) )
            .thenReturn( this.dummyDoc );

        this.handler.handle( this.request );

        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getContentByQuery(
            eq( request ),
            eq( "title STARTS WITH 'Content '" ),
            eq( "@title asc" ),
            eq( 0 ),
            eq( 10 ),
            eq( true ),
            eq( 1 ),
            eq( 0 ) );
    }
}
