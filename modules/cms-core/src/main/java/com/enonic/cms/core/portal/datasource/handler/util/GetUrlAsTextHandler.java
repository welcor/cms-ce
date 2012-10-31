package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetUrlAsTextHandler")
public final class GetUrlAsTextHandler
    extends ParamsDataSourceHandler<GetUrlAsTextParams>
{
    private HTTPService httpService;

    private static String URL_NO_RESULT = "<noresult/>";

    public GetUrlAsTextHandler()
    {
        super( "getUrlAsText", GetUrlAsTextParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetUrlAsTextParams params )
        throws Exception
    {
        final String urlResult = this.httpService.getURL( params.url, params.encoding, params.timeout );
        final Element root = new Element( "urlresult" );
        root.setText( urlResult );
        return new Document( root );
    }

    @Autowired
    public void setHttpService( final HTTPService httpService )
    {
        this.httpService = httpService;
    }
}
