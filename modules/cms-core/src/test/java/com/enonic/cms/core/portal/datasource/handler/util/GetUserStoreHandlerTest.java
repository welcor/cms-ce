/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetUserStoreHandlerTest
    extends AbstractDataSourceHandlerTest<GetUserStoreHandler>
{
    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetUserStoreHandlerTest()
    {
        super( GetUserStoreHandler.class );
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
    public void testNullName()
        throws Exception
    {
        Mockito.when( this.dataSourceService.getUserstore( this.request, null ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getUserstore( this.request, null );
    }

    @Test
    public void testEmptyName()
        throws Exception
    {
        this.request.addParam( "name", "" );
        Mockito.when( this.dataSourceService.getUserstore( this.request, "" ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getUserstore( this.request, "" );
    }

    @Test
    public void testDummyName()
        throws Exception
    {
        this.request.addParam( "name", "dummy" );
        Mockito.when( this.dataSourceService.getUserstore( this.request, "dummy" ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getUserstore( this.request, "dummy" );
    }
}
