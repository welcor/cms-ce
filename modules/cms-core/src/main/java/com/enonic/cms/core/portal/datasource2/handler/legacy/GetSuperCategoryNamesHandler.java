package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

public final class GetSuperCategoryNamesHandler
    extends DataSourceHandler
{

    private DataSourceService dataSourceService;

    public GetSuperCategoryNamesHandler()
    {
        super( "getSuperCategoryNames" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int categoryKey = req.param( "categoryKey" ).required().asInteger();
        final boolean includeContentCount = req.param( "includeContentCount" ).asBoolean( false );
        final boolean includeCurrent = req.param( "includeCurrent" ).asBoolean( false );

        return this.dataSourceService.getSuperCategoryNames( req, categoryKey, includeContentCount, includeCurrent ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
