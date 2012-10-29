package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetContentVersionHandler")
public final class GetContentVersionHandler
    extends ParamsDataSourceHandler<GetContentVersionParams>
{
    public GetContentVersionHandler()
    {
        super( "getContentVersion", GetContentVersionParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetContentVersionParams params )
        throws Exception
    {
        XMLDocument document = dataSourceService.getContentVersion( req, params.versionKeys, params.childrenLevel );
        return document.getAsJDOMDocument();
    }
}
