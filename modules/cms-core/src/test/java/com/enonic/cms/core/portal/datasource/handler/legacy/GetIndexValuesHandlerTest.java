package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

import static org.mockito.Matchers.eq;

public class GetIndexValuesHandlerTest
    extends AbstractDataSourceHandlerTest<GetIndexValuesHandler>
{
    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetIndexValuesHandlerTest()
    {
        super( GetIndexValuesHandler.class );
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
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "distinct", "true" );
        this.request.addParam( "order", "DESC" );
        this.request.addParam( "contentTypeKeys", "100,200,300" );
    }

    @Test
    public void testHandler_get_index_values()
        throws Exception
    {
        Mockito.when(
            this.dataSourceService.getIndexValues(
                eq( request ),
                eq( "data.age" ),
                eq( new int[]{10, 20, 30} ),
                eq( true ),
                eq( new int[]{100, 200, 300} ),
                eq( 0 ),
                eq( 10 ),
                eq( true ),
                eq( "DESC" ) ) )
            .thenReturn( this.dummyDoc );

        this.handler.handle( this.request );

        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getIndexValues(
            eq( request ),
            eq( "data.age" ),
            eq( new int[]{10, 20, 30} ),
            eq( true ),
            eq( new int[]{100, 200, 300} ),
            eq( 0 ),
            eq( 10 ),
            eq( true ),
            eq( "DESC" ) );
    }
}
