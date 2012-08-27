/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.web.filter.GenericFilterBean;

public final class ASCIICharactersFilter
    extends GenericFilterBean
{

    private final static Logger LOG = Logger.getLogger( ASCIICharactersFilter.class.getName() );

    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
        throws IOException, ServletException
    {

        try
        {
            doFilter( (HttpServletRequest) req, (HttpServletResponse) res, chain );
        }
        catch ( IOException e )
        {
            throw e;
        }
        catch ( ServletException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            LOG.log( Level.SEVERE, e.getMessage(), e );
            throw new ServletException( e );
        }
    }

    private void doFilter( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
        throws Exception
    {
        chain.doFilter( new FilteredRequest( req ), res );
    }

    static class FilteredRequest
        extends HttpServletRequestWrapper
    {
        public FilteredRequest( ServletRequest request )
        {
            super( (HttpServletRequest) request );
        }

        @Override
        public String getParameter( String paramName )
        {
            final String value = super.getParameter( paramName );
            return sanitize( value );
        }

        @Override
        public String[] getParameterValues( String paramName )
        {
            final String values[] = super.getParameterValues( paramName );
            if ( values == null )
            {
                return null;
            }

            for ( int index = 0; index < values.length; index++ )
            {
                values[index] = sanitize( values[index] );
            }

            return values;
        }

        @Override
        public Map<String, String[]> getParameterMap()
        {
            final Map<String, String[]> map = new HashMap<String, String[]>();
            final Enumeration<String> parameterNames = getParameterNames();

            // No need to check the inherit parameter, since the getParameterNames() and getParameterValues() methods do it.
            while ( parameterNames.hasMoreElements() )
            {
                final String parameterName = parameterNames.nextElement();
                map.put( parameterName, getParameterValues( parameterName ) );
            }

            return map;
        }

        /**
         * removes non-ascii characters except \r \t
         * @param string to process
         * @return cleaned string
         */
        private String sanitize( String string )
        {
            if (string == null)
            {
                return string;
            }

            final char[] chars = string.toCharArray();
            final StringBuilder stringBuilder = new StringBuilder();

            for ( final char c : chars )
            {
                if ( c < ' ' )
                {
                    if ( c == '\r' || c == '\t' )
                    {
                        stringBuilder.append( c );
                    }
                }
                else
                {
                    if ( c != 127 )
                    {
                        stringBuilder.append( c );
                    }
                }
            }

            return stringBuilder.toString();
        }
    }
}
