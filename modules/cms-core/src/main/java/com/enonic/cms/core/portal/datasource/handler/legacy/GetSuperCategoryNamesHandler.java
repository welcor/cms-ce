package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetSuperCategoryNamesHandler")
public final class GetSuperCategoryNamesHandler
    extends ParamsDataSourceHandler<GetSuperCategoryNamesParams>
{
    public GetSuperCategoryNamesHandler()
    {
        super( "getSuperCategoryNames", GetSuperCategoryNamesParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetSuperCategoryNamesParams params )
        throws Exception
    {
        return this.dataSourceService.getSuperCategoryNames( req, params.categoryKey, params.includeContentCount,
                                                             params.includeCurrent ).getAsJDOMDocument();
    }
}
