/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.menu;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

public class GetMenuBranchHandlerTest
    extends AbstractDataSourceHandlerTest<GetMenuBranchHandler>
{

    private DataSourceService dataSourceService;

    private XMLDocument dummyDoc;

    public GetMenuBranchHandlerTest()
    {
        super( GetMenuBranchHandler.class );
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
    public void testHandler_get_menu_branch()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "42" );
        this.request.addParam( "includeTopLevel", "false" );
        this.request.addParam( "startLevel", "0" );
        this.request.addParam( "levels", "0" );

        Mockito.when( this.dataSourceService.getMenuBranch( this.request, 42, false, 0, 0 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuBranch( this.request, 42, false, 0, 0 );
    }

    @Test
    public void testHandler_get_menu_branch_include_top_levels()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "42" );
        this.request.addParam( "includeTopLevel", "true" );
        this.request.addParam( "startLevel", "0" );
        this.request.addParam( "levels", "0" );

        Mockito.when( this.dataSourceService.getMenuBranch( this.request, 42, true, 0, 0 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuBranch( this.request, 42, true, 0, 0 );
    }

    @Test
    public void testHandler_get_menu_branch_with_levels()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "42" );
        this.request.addParam( "includeTopLevel", "true" );
        this.request.addParam( "startLevel", "1" );
        this.request.addParam( "levels", "3" );

        Mockito.when( this.dataSourceService.getMenuBranch( this.request, 42, true, 1, 3 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuBranch( this.request, 42, true, 1, 3 );
    }

    @Test
    public void testHandler_default_values()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "42" );

        Mockito.when( this.dataSourceService.getMenuBranch( this.request, 42, false, 0, 0 ) ).thenReturn( this.dummyDoc );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getMenuBranch( this.request, 42, false, 0, 0 );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_invalid_parameter_type()
        throws Exception
    {
        this.request.addParam( "menuItemKey", "42,55" );
        this.request.addParam( "includeTopLevel", "false" );
        this.request.addParam( "startLevel", "0" );
        this.request.addParam( "levels", "0" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        this.request.addParam( "includeTopLevel", "false" );
        this.request.addParam( "startLevel", "0" );
        this.request.addParam( "levels", "0" );

        this.handler.handle( this.request );
    }
}
