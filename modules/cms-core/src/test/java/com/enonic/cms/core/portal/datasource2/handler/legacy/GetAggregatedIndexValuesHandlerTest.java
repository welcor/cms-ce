package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

import static org.mockito.Matchers.eq;

public class GetAggregatedIndexValuesHandlerTest
    extends AbstractDataSourceHandlerTest<GetAggregatedIndexValuesHandler>
{
    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetAggregatedIndexValuesHandlerTest()
    {
        super( GetAggregatedIndexValuesHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.dummyDoc = XMLDocumentFactory.create( "<dummy/>" );
        this.dataSourceService = Mockito.mock( DataSourceService.class );
        this.handler.setDataSourceService( this.dataSourceService );

        this.request.addParam( "field", "data.age" );
        this.request.addParam( "categoryKeys", "10,20,30" );
        this.request.addParam( "recursive", "true" );
        this.request.addParam( "contentTypeKeys", "100,200,300" );
    }

    @Test
    public void testHandler_get_aggregated_index_values()
        throws Exception
    {
        Mockito.when(
            this.dataSourceService.getAggregatedIndexValues(
                eq( request ),
                eq( "data.age" ),
                eq( new int[]{10, 20, 30} ),
                eq( true ),
                eq( new int[]{100, 200, 300} ) ) )
            .thenReturn( this.dummyDoc );

        this.handler.handle( this.request );

        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getAggregatedIndexValues(
            eq( request ),
            eq( "data.age" ),
            eq( new int[]{10, 20, 30} ),
            eq( true ),
            eq( new int[]{100, 200, 300} ) );
    }
}
