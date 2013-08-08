/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class CookieAccessor
    implements Accessor<String>
{
    private final HttpServletRequest httpRequest;

    public CookieAccessor( final HttpServletRequest httpRequest )
    {

        this.httpRequest = httpRequest;
    }

    @Override
    public String getValue( final String name )
    {
        if ( this.httpRequest != null )
        {
            final Cookie[] cookies = httpRequest.getCookies();

            if ( cookies != null )
            {
                for ( final Cookie cookie : cookies )
                {
                    if ( name.equals( cookie.getName() ) )
                    {
                        return cookie.getValue();
                    }
                }
            }
        }

        return null;
    }

}
