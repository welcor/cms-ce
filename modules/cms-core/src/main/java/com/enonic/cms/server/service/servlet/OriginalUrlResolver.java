/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.server.service.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.core.Attribute;


public class OriginalUrlResolver
{
    public static String buildOriginalUrl( HttpServletRequest req )
    {
        final UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setDefaultEncoding( "UTF-8" );

        final StringBuilder url = new StringBuilder();
        url.append( req.getScheme() ).append( "://" );
        url.append( req.getServerName() );

        final int serverPort = req.getServerPort();
        if ( serverPort != 80 )
        {
            url.append( ":" ).append( serverPort );
        }

        final String originatingUri = urlPathHelper.getOriginatingRequestUri( req );
        url.append( originatingUri );

        final String originatingQueryString = urlPathHelper.getOriginatingQueryString( req );
        if ( originatingQueryString != null && !"".equals( originatingQueryString ) )
        {
            url.append( "?" ).append( originatingQueryString );
        }

        return url.toString();
    }

    public static void resolveOriginalUrl( final HttpServletRequest req )
    {
        // resolve and set original url if not set
        if ( req.getAttribute( Attribute.ORIGINAL_URL ) == null )
        {
            final String originalUrl = buildOriginalUrl( req );
            req.setAttribute( Attribute.ORIGINAL_URL, originalUrl );
        }
    }

}
