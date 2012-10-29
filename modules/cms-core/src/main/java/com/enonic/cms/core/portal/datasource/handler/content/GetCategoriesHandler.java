package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetCategoriesHandler")
public final class GetCategoriesHandler
    extends ParamsDataSourceHandler<GetCategoriesParams>
{
    public GetCategoriesHandler()
    {
        super( "getCategories", GetCategoriesParams.class );
    }

    @Override
    public Document handle( final DataSourceRequest req, final GetCategoriesParams params )
        throws Exception
    {
        return this.dataSourceService.getCategories( req, params.categoryKey, params.levels, params.includeContentCount,
                                                     params.includeTopCategory ).getAsJDOMDocument();
    }
}
