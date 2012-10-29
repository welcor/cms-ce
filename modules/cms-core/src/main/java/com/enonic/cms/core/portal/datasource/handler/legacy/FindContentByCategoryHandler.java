package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.FindContentByCategoryHandler")
public final class FindContentByCategoryHandler
    extends ParamsDataSourceHandler<FindContentByCategoryParams>
{
    public FindContentByCategoryHandler()
    {
        super( "findContentByCategory", FindContentByCategoryParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final FindContentByCategoryParams params )
        throws Exception
    {
        return dataSourceService.findContentByCategory( req, params.search, params.operator, params.categories, params.includeSubCategories,
                                                        params.orderBy, params.index, params.count, params.titlesOnly, params.childrenLevel,
                                                        params.parentLevel, params.parentChildrenLevel, params.relatedTitlesOnly,
                                                        params.includeTotalCount, params.includeUserRights,
                                                        params.contentTypes ).getAsJDOMDocument();
    }
}
