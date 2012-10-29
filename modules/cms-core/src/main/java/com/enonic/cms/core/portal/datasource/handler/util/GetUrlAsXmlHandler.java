package com.enonic.cms.core.portal.datasource.handler.util;

import java.io.ByteArrayInputStream;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetUrlAsXmlHandler")
public final class GetUrlAsXmlHandler
    extends ParamsDataSourceHandler<GetUrlAsXmlParams>
{
    private HTTPService httpService;

    public GetUrlAsXmlHandler()
    {
        super( "getUrlAsXml", GetUrlAsXmlParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetUrlAsXmlParams params )
        throws Exception
    {
        final byte[] data = this.httpService.getURLAsBytes( params.url, params.timeout );
        return JDOMUtil.parseDocument( new ByteArrayInputStream( data ) );
    }

    @Autowired
    public void setHttpService( final HTTPService httpService )
    {
        this.httpService = httpService;
    }
}
