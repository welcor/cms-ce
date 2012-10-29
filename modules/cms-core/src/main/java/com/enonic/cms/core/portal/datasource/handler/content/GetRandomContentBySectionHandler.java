package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetRandomContentBySectionHandler")
public final class GetRandomContentBySectionHandler
    extends ParamsDataSourceHandler<GetRandomContentBySectionParams>
{
    public GetRandomContentBySectionHandler()
    {
        super( "getRandomContentBySection", GetRandomContentBySectionParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetRandomContentBySectionParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getRandomContentBySection( req, params.menuItemKeys, params.levels, params.query, params.count,
                                                         params.includeData, params.childrenLevel, params.parentLevel );
        return document.getAsJDOMDocument();
    }
}
