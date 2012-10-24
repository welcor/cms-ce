package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.http.HTTPService;

public final class GetUrlAsTextHandler
    extends DataSourceHandler
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
        final String url = req.param( "url" ).required().asString();
        final String encoding = req.param( "encoding" ).asString( "ISO-8859-1" );
        final int timeout = req.param( "timeout" ).asInteger( 5000 );

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
