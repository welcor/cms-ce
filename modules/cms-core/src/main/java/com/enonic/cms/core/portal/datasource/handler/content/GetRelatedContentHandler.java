/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetRelatedContentHandler")
public final class GetRelatedContentHandler
    extends ParamsDataSourceHandler<GetRelatedContentParams>
{
    public GetRelatedContentHandler()
    {
        super( "getRelatedContent", GetRelatedContentParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetRelatedContentParams params )
        throws Exception
    {
        return this.dataSourceService.getRelatedContent( req, params.contentKeys, params.relation, params.query, params.orderBy,
                                                         params.index, params.count, params.includeData, params.childrenLevel,
                                                         params.parentLevel, params.requireAll, params.facets ).getAsJDOMDocument();
    }
}
