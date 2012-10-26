package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

@Component("ds.GetUrlAsTextHandler")
public final class GetUrlAsTextHandler
    extends ParamDataSourceHandler
{
    private HTTPService httpService;

    public GetUrlAsTextHandler()
    {
        super( "getUrlAsText" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String url = param(req, "url" ).required().asString();
        final String encoding = param(req, "encoding" ).asString( "ISO-8859-1" );
        final int timeout = param(req, "timeout" ).asInteger( 5000 );

        final Element root = new Element( "urlresult" );
        root.setText( this.httpService.getURL( url, encoding, timeout ) );
        return new Document( root );
    }

    @Autowired
    public void setHttpService( final HTTPService httpService )
    {
        this.httpService = httpService;
    }
}
