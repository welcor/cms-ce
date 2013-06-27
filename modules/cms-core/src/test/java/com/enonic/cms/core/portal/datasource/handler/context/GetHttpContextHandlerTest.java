/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.context;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;

public class GetHttpContextHandlerTest
    extends AbstractDataSourceHandlerTest<GetHttpContextHandler>
{
    public GetHttpContextHandlerTest()
    {
        super( GetHttpContextHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.handler.setDataSourceService( this.dataSourceService );
    }

    @Test
    public void testHandler_get_http_context()
        throws Exception
    {
        final MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader( "user-agent", "NCSA Mosaic/3.0 (Windows 95)" );
        httpRequest.addHeader( "referer", "http://en.wikipedia.org/wiki/HTTP_referer" );
        httpRequest.addHeader( "accept-language", "en-US,no;q=0.7,ca;q=0.3" );
        httpRequest.setRemoteAddr( "192.168.13.37" );
        httpRequest.setMethod( "POST" );
        this.request.setHttpRequest( httpRequest );

        testHandle( "getHttpContext_result" );
    }
}
