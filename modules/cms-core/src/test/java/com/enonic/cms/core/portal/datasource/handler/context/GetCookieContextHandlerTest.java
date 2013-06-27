/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;

public class GetCookieContextHandlerTest
    extends AbstractDataSourceHandlerTest<GetCookieContextHandler>
{
    public GetCookieContextHandlerTest()
    {
        super( GetCookieContextHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.handler.setDataSourceService( this.dataSourceService );
    }

    @Test
    public void testHandler_get_cookie_context()
        throws Exception
    {
        final MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        final Cookie cookie = new Cookie( "mycookie1", "value1" );
        final Cookie cookie2 = new Cookie( "mycookie2", "true" );
        final Cookie cookie3 = new Cookie( "mycookie3", "33" );
        httpRequest.setCookies( cookie, cookie2, cookie3 );
        this.request.setHttpRequest( httpRequest );

        testHandle( "getCookieContext_result" );
    }

    @Test
    public void testHandler_no_cookies()
        throws Exception
    {
        final HttpServletRequest httpRequest = new MockHttpServletRequest();
        this.request.setHttpRequest( httpRequest );

        testHandle( "getCookieContext_empty" );
    }
}
