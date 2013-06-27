/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetRandomContentByCategoryHandler")
public final class GetRandomContentByCategoryHandler
    extends ParamsDataSourceHandler<GetRandomContentByCategoryParams>
{
    public GetRandomContentByCategoryHandler()
    {
        super( "getRandomContentByCategory", GetRandomContentByCategoryParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetRandomContentByCategoryParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getRandomContentByCategory( req, params.categoryKeys, params.levels, params.query, params.count,
                                                          params.includeData, params.childrenLevel, params.parentLevel );
        return document.getAsJDOMDocument();
    }
}
