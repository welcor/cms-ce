package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

@Component("ds.GetSuperCategoryNamesHandler")
public final class GetSuperCategoryNamesHandler
    extends ParamDataSourceHandler
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
        final int categoryKey = param( req, "categoryKey" ).required().asInteger();
        final boolean includeContentCount = param( req, "includeContentCount" ).asBoolean( false );
        final boolean includeCurrent = param( req, "includeCurrent" ).asBoolean( false );

        return this.dataSourceService.getSuperCategoryNames( req, categoryKey, includeContentCount, includeCurrent ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
