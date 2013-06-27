/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetMyContentByCategoryHandler")
public final class GetMyContentByCategoryHandler
    extends ParamsDataSourceHandler<GetMyContentByCategoryParams>
{
    public GetMyContentByCategoryHandler()
    {
        super( "getMyContentByCategory", GetMyContentByCategoryParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetMyContentByCategoryParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getMyContentByCategory( req, params.query, params.categoryKeys, params.recursive, params.orderBy,
                                                      params.index, params.count, params.titlesOnly, params.childrenLevel,
                                                      params.parentLevel, params.parentChildrenLevel, params.relatedTitlesOnly,
                                                      params.includeTotalCount, params.includeUserRights, params.contentTypeKeys );
        return document.getAsJDOMDocument();
    }
}
