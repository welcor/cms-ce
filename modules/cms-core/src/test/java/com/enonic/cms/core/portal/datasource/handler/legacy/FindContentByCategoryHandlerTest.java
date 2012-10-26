package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.service.DataSourceService;

import static org.mockito.Matchers.eq;

public class FindContentByCategoryHandlerTest
    extends AbstractDataSourceHandlerTest<FindContentByCategoryHandler>
{
    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public FindContentByCategoryHandlerTest()
    {
        super( FindContentByCategoryHandler.class );
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
        this.request.addParam( "search", "1" );
        this.request.addParam( "operator", "2" );
        this.request.addParam( "categories", "1,2,3" );
        this.request.addParam( "includeSubCategories", "true" );
        this.request.addParam( "orderBy", "a" );
        this.request.addParam( "index", "1" );
        this.request.addParam( "count", "2" );
        this.request.addParam( "titlesOnly", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "2" );
        this.request.addParam( "parentChildrenLevel", "3" );
        this.request.addParam( "relatedTitlesOnly", "true" );
        this.request.addParam( "includeTotalCount", "true" );
        this.request.addParam( "includeUserRights", "true" );
        this.request.addParam( "contentTypes", "1,2" );

        Mockito.when(
            dataSourceService.findContentByCategory( Mockito.<DataSourceContext>any(), eq( "1" ), eq( "2" ), eq( new int[]{1, 2, 3} ),
                                                     eq( true ), eq( "a" ), eq( 1 ), eq( 2 ), eq( true ), eq( 1 ), eq( 2 ), eq( 3 ),
                                                     eq( true ), eq( true ), eq( true ), eq( new int[]{1, 2} ) ) ).thenReturn(
            this.dummyDoc );

        testHandle( "findContentByCategory_dummy" );
    }

}