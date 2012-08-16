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
            String value = super.getParameter( paramName );
            return doParse( value );
        }

        @Override
        public String[] getParameterValues( String paramName )
        {
            String values[] = super.getParameterValues( paramName );
            if ( values == null )
            {
                return null;
            }

            for ( int index = 0; index < values.length; index++ )
            {
                values[index] = doParse( values[index] );
            }

            return values;
        }

        @Override
        public Map<String, String[]> getParameterMap()
        {
            Map<String, String[]> map = new HashMap<String, String[]>();
            Enumeration<String> enumer = getParameterNames();

            // No need to check the inherit parameter, since the getParameterNames() and getParameterValues() methods do it.
            while ( enumer.hasMoreElements() )
            {
                String s = enumer.nextElement();
                map.put( s, getParameterValues( s ) );
            }

            return map;
        }

        private String sanitize( String input )
        {
            char[] chars = input.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();

            for ( char c : chars )
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

        private String doParse( String source )
        {
            return null == source ? source : doParseQueryString( source );
        }

        private MultiValueMap parseQueryString( String queryString )
        {
            MultiValueMap queryParams = new MultiValueMap();
            int index = queryString.indexOf( '?' );
            String noBaseQuery = queryString;
            index = ( index == 0 ) ? 1 : index + 1;

            if ( index != -1 )
            {
                noBaseQuery = queryString.substring( index );
            }

            StringTokenizer tokenizer = new StringTokenizer( noBaseQuery, "&" );

            while ( tokenizer.hasMoreTokens() )
            {
                String token = tokenizer.nextToken();
                String name;
                String value;
                int equalIdx = token.indexOf( '=' );

                if ( equalIdx == -1 )
                {
                    name = "";
                    value = token;
                }
                else
                {
                    name = token.substring( 0, equalIdx );
                    value = token.substring( equalIdx + 1 );
                }

                if ( value != null )
                {
                    String healthyValue = sanitize( value );
                    queryParams.put( name, healthyValue );
                }
            }
            return queryParams;
        }

        private String doParseQueryString( String queryString )
        {
            MultiValueMap queryParams = parseQueryString( queryString );
            String baseURL = composeBaseURL( queryString );

            return doComposeURL( queryParams, baseURL );
        }

        private String composeBaseURL( String queryString )
        {
            int index = queryString.indexOf( '?' );
            return index != -1 ? queryString.substring( 0, index + 1 ) : "";
        }

        private String doComposeURL( MultiValueMap queryParams, String baseURL )
        {
            StringBuilder url = new StringBuilder( baseURL );
            Iterator keyIterator = queryParams.keySet().iterator();
            boolean firstParam = true;

            while ( keyIterator.hasNext() )
            {
                String key = (String) keyIterator.next();

                if ( "".equals( key ) && !keyIterator.hasNext() )
                {
                    return (String) queryParams.values().iterator().next();
                }

                Iterator iter = queryParams.iterator( key );
                while ( iter.hasNext() )
                {
                    Object value = iter.next();
                    if ( firstParam )
                    {
                        firstParam = false;
                    }
                    else
                    {
                        url.append( '&' );
                    }
                    url.append( key );
                    url.append( '=' );
                    url.append( value );
                }
            }

            return url.toString();
        }
    }
}
