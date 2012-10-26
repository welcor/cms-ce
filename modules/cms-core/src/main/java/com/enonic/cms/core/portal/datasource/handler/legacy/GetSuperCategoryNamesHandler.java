package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

@Component("ds.GetSuperCategoryNamesHandler")
public final class GetSuperCategoryNamesHandler
    extends ParamDataSourceHandler
{
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
}
