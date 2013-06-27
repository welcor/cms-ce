/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.stereotype.Component;

import com.enonic.esl.net.URLUtil;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetCookieContextHandler")
public final class GetCookieContextHandler
    extends SimpleDataSourceHandler
{
    public GetCookieContextHandler()
    {
        super( "getCookieContext" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        return new Document( createCookieElement( req.getHttpRequest() ) );
    }

    private Element createCookieElement( final HttpServletRequest request )
    {
        final Element cookiesElem = new Element( "cookies" );

        final Cookie[] cookies = request != null ? request.getCookies() : null;
        if ( cookies != null )
        {
            for ( Cookie cookie : cookies )
            {
                final Element cookieElem = new Element( "cookie" );
                cookieElem.setText( URLUtil.decode( cookie.getValue() ) );
                cookieElem.setAttribute( "name", URLUtil.decode( cookie.getName() ) );
                cookiesElem.addContent( cookieElem );
            }
        }
        return cookiesElem;
    }
}
