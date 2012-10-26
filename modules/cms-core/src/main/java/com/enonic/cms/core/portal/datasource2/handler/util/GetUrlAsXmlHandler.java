package com.enonic.cms.core.portal.datasource2.handler.util;

import java.io.ByteArrayInputStream;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

public final class GetUrlAsXmlHandler
    extends ParamDataSourceHandler
{
    private HTTPService httpService;

    public GetUrlAsXmlHandler()
    {
        super( "getUrlAsXml" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String url = param(req, "url" ).required().asString();
        final int timeout = param(req, "timeout" ).asInteger( 5000 );

        final byte[] data = this.httpService.getURLAsBytes( url, timeout );
        return JDOMUtil.parseDocument( new ByteArrayInputStream( data ) );
    }

    @Autowired
    public void setHttpService( final HTTPService httpService )
    {
        this.httpService = httpService;
    }
}
