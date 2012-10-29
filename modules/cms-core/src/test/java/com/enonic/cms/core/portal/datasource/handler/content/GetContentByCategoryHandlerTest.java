package com.enonic.cms.core.portal.datasource.handler.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

import static org.mockito.Matchers.eq;

public class GetContentByCategoryHandlerTest
    extends AbstractDataSourceHandlerTest<GetContentByCategoryHandler>
{
    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetContentByCategoryHandlerTest()
    {
        super( GetContentByCategoryHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        dataSourceService = Mockito.mock( DataSourceService.class );
        handler.setDataSourceService( dataSourceService );
        this.dummyDoc = XMLDocumentFactory.create( "<dummy/>" );
    }

    @Test
    public void testHandle_by_categoryKeys()
        throws Exception
    {
        Mockito.when(
            dataSourceService.getContentByCategory( Mockito.<DataSourceContext>any(), eq( new int[]{11, 12, 13} ), eq( 2 ), eq( "a > 5" ),
                                                    eq( "a" ), eq( 0 ), eq( 21 ), eq( true ), eq( 1 ), eq( 0 ), false ) ).thenReturn(
            this.dummyDoc );

        this.request.addParam( "categoryKeys", "11,12,13" );
        this.request.addParam( "levels", "2" );
        this.request.addParam( "query", "a > 5" );
        this.request.addParam( "orderBy", "a" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "21" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        testHandle( "getContentByCategory_dummy" );
    }
}
