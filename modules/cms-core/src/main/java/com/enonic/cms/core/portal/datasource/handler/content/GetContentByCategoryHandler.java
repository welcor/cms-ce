package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

import static org.apache.commons.lang.ArrayUtils.toPrimitive;

@Component("ds.GetContentByCategoryHandler")
public final class GetContentByCategoryHandler
    extends ParamsDataSourceHandler<GetContentByCategoryParams>
{
    public GetContentByCategoryHandler()
    {
        super( "getContentByCategory", GetContentByCategoryParams.class );
    }

    @Override
    public Document handle( final DataSourceRequest req, final GetContentByCategoryParams params )
        throws Exception
    {
        // TODO: To be implemented, see getMyContentByCategory in DatasourceServiceImpl
        // final boolean filterOnUser = param( req, "filterOnUser" ).asBoolean( false );

        return dataSourceService.getContentByCategory( req, toPrimitive( params.categoryKeys ), params.levels, params.query, params.orderBy,
                                                       params.index, params.count, params.includeData, params.childrenLevel,
                                                       params.parentLevel, params.filterOnUser, params.facets ).getAsJDOMDocument();
    }
}
