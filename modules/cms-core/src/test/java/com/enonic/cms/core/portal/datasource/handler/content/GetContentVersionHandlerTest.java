package com.enonic.cms.core.portal.datasource.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource.AbstractDataSourceHandlerTest;

import static org.mockito.Matchers.eq;

public class GetContentVersionHandlerTest
    extends AbstractDataSourceHandlerTest<GetContentVersionHandler>
{
    public GetContentVersionHandlerTest()
    {
        super( GetContentVersionHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.handler.setDataSourceService( this.dataSourceService );

        this.request.addParam( "versionKeys", "10,20,30" );
        this.request.addParam( "childrenLevel", "1" );
    }

    @Test
    public void testHandler_get_content_by_query()
        throws Exception
    {
        Mockito.when( this.dataSourceService.getContentVersion( eq( request ), eq( new int[]{10, 20, 30} ), eq( 1 ) ) ).thenReturn(this.dummyDoc );

        this.handler.handle( this.request );

        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getContentVersion( eq( request ), eq( new int[]{10, 20, 30} ), eq( 1 ) );
    }
}
