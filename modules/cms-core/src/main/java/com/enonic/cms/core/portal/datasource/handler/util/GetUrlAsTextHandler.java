package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetUrlAsTextHandler")
public final class GetUrlAsTextHandler
    extends ParamsDataSourceHandler<GetUrlAsTextParams>
{
    private HTTPService httpService;

    public GetUrlAsTextHandler()
    {
        super( "getUrlAsText", GetUrlAsTextParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetUrlAsTextParams params )
        throws Exception
    {
        final Element root = new Element( "urlresult" );
        root.setText( this.httpService.getURL( params.url, params.encoding, params.timeout ) );
        return new Document( root );
    }

    @Autowired
    public void setHttpService( final HTTPService httpService )
    {
        this.httpService = httpService;
    }
}
