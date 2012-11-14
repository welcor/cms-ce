package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetContentHandler")
public final class GetContentHandler
    extends ParamsDataSourceHandler<GetContentParams>
{
    public GetContentHandler()
    {
        super( "getContent", GetContentParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetContentParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getContent( req, params.contentKeys, params.query, params.orderBy, params.index, params.count,
                                          params.includeData, params.childrenLevel, params.parentLevel, params.facets );
        return document.getAsJDOMDocument();
    }
}
