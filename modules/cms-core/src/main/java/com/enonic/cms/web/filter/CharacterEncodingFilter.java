/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public final class CharacterEncodingFilter
    extends OncePerRequestFilter
{
    private String encoding;

    @Value("${cms.url.characterEncoding}")
    public void setCharacterEncoding( final String encoding )
    {
        this.encoding = encoding;
    }

    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
        throws ServletException, IOException
    {
        if ( request.getCharacterEncoding() == null )
        {
            request.setCharacterEncoding( encoding );
        }

        final String forcedCharset = request.getParameter( "_charset" );
        if ( !StringUtils.isBlank( forcedCharset ) )
        {
            response.setCharacterEncoding( forcedCharset );
        }
        else
        {
            response.setCharacterEncoding( encoding );
        }

        filterChain.doFilter( request, response );
    }
}
