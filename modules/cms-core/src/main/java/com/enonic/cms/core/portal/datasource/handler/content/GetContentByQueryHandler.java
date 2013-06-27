/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetContentByQueryHandler")
public final class GetContentByQueryHandler
    extends ParamsDataSourceHandler<GetContentByQueryParams>
{
    public GetContentByQueryHandler()
    {
        super( "getContentByQuery", GetContentByQueryParams.class );
    }

    @Override
    public Document handle( final DataSourceRequest req, final GetContentByQueryParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getContentByQuery( req, params.query, params.orderBy, params.index, params.count, params.includeData,
                                                 params.childrenLevel, params.parentLevel, params.facets );
        return document.getAsJDOMDocument();
    }
}
