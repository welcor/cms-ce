/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import org.jdom.Document;

import com.enonic.cms.api.client.model.GetCategoriesParams;
import com.enonic.cms.api.client.model.GetMenuDataParams;
import com.enonic.cms.api.client.model.GetMenuItemParams;
import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.portal.datasource.service.DataSourceService;

/**
 * This class wraps the presentation service and calls with the new client api.
 */
public final class PresentationInvoker
{

    private final DataSourceService dataSourceService;

    private final SecurityService securityService;

    public PresentationInvoker( DataSourceService dataSourceService, SecurityService securityService )
    {
        this.dataSourceService = dataSourceService;
        this.securityService = securityService;
    }

    private DataSourceContext createDataSourceContext()
    {
        DataSourceContext dataSourceContext = new DataSourceContext();
        dataSourceContext.setUser( securityService.getImpersonatedPortalUser() );
        return dataSourceContext;
    }

    public Document getCategories( GetCategoriesParams params )
        throws Exception
    {
        assertMinValue( "categoryKey", params.categoryKey, 0 );
        return this.dataSourceService.getCategories( createDataSourceContext(), params.categoryKey, params.levels,
                                                     params.includeTopCategory, true, false, params.includeContentCount ).getAsJDOMDocument();
    }

    public Document getMenuData( GetMenuDataParams params )
        throws Exception
    {
        assertMinValue( "menuKey", params.menuKey, 0 );

        return this.dataSourceService.getMenuData( createDataSourceContext(), params.menuKey ).getAsJDOMDocument();
    }

    public Document getMenuItem( GetMenuItemParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );

        return this.dataSourceService.getMenuItem( createDataSourceContext(), params.menuItemKey, params.withParents ).getAsJDOMDocument();
    }

    private void assertMinValue( String name, int value, int minValue )
    {
        if ( value < minValue )
        {
            throw new IllegalArgumentException( "Parameter [" + name + "] must be >= " + minValue );
        }
    }
}
