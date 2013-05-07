/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.binrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.api.client.ClientException;

/**
 * This class implements the invocation handler.
 */
public final class BinRpcInvocationHandler
    implements InvocationHandler
{
    /**
     * Session id cookie.
     */
    private final static String SESSION_COOKIE_NAME = "JSESSIONID";

    /**
     * Content type.
     */
    private final static String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    /**
     * Http method post.
     */
    private final static String HTTP_METHOD_POST = "POST";

    /**
     * Http cookie header.
     */
    private final static String HTTP_HEADER_COOKIE = "Cookie";

    /**
     * Http set-cookie header.
     */
    private final static String HTTP_HEADER_SET_COOKIE = "Set-Cookie";

    /**
     * Http content type.
     */
    private final static String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Http content length.
     */
    private final static String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";

    /**
     * Connection url.
     */
    private final String serviceUrl;

    /**
     * Use global session.
     */
    private final boolean useGlobalSession;

    /**
     * Session id per thread.
     */
    private final ThreadLocal<String> localSessionIds;


    private final ThreadLocal<Map<String, String>> localCookies;

    /**
     * Current session id.
     */
    private String globalSessionId;

    /**
     * Construct the invocation handler.
     */
    public BinRpcInvocationHandler( String serviceUrl, boolean useGlobalSession )
    {
        this.serviceUrl = serviceUrl;
        this.useGlobalSession = useGlobalSession;
        this.localSessionIds = new ThreadLocal<String>();
        this.localCookies = new ThreadLocal<Map<String, String>>();
    }

    /**
     * Invoke the method.
     */
    public Object invoke( Object object, Method method, Object[] args )
        throws Throwable
    {
        try
        {
            String methodName = method.getName();
            Class<?>[] params = method.getParameterTypes();
            BinRpcInvocation invocation = new BinRpcInvocation( methodName, params, args );
            BinRpcInvocationResult result = executeRequest( invocation );
            return result.recreate();
        }
        catch ( ClientException e )
        {
            throw e;
        }
        catch ( UndeclaredThrowableException e )
        {
            throw new ClientException( e.getUndeclaredThrowable() );
        }
        catch ( Throwable e )
        {
            throw new ClientException( e );
        }
    }

    /**
     * Open connection.
     */
    private HttpURLConnection openConnection()
        throws IOException
    {
        URLConnection conn = new URL( this.serviceUrl ).openConnection();
        if ( !( conn instanceof HttpURLConnection ) )
        {
            throw new ClientException( "Service URL [" + this.serviceUrl + "] is not an HTTP URL" );
        }

        return (HttpURLConnection) conn;
    }

    /**
     * Execute the request.
     */
    private BinRpcInvocationResult executeRequest( BinRpcInvocation invocation )
        throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = getByteArrayOutputStream( invocation );
        HttpURLConnection conn = openConnection();
        prepareConnection( conn, baos.size() );
        writeRequestBody( conn, baos );
        validateResponse( conn );
        checkResponseHeaders( conn );
        return readResponseBody( conn );
    }

    /**
     * Prepare connection.
     */
    private void prepareConnection( HttpURLConnection conn, int contentLength )
        throws IOException
    {
        conn.setDoOutput( true );
        conn.setRequestMethod( HTTP_METHOD_POST );
        conn.setRequestProperty( HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_SERIALIZED_OBJECT );
        conn.setRequestProperty( HTTP_HEADER_CONTENT_LENGTH, Integer.toString( contentLength ) );

        if ( getSessionId() != null )
        {
            conn.setRequestProperty( HTTP_HEADER_COOKIE, createCookieString() );
        }
    }

    private String createCookieString()
    {
        StringBuffer buf = new StringBuffer();

        if ( getSessionId() != null )
        {
            buf.append( createSessionCookie() );
        }

        final Map<String, String> localCookies = this.localCookies.get();
        if ( localCookies != null && localCookies.size() > 0 )
        {
            buf.append( createLocalCookies( localCookies ) );
        }

        return buf.toString();
    }

    private String createLocalCookies( Map<String, String> localCookies )
    {
        StringBuffer buf = new StringBuffer();

        for ( String cookieKey : localCookies.keySet() )
        {
            buf.append( cookieKey ).append( "=" ).append( localCookies.get( cookieKey ) ).append( ";" );
        }

        return buf.toString();
    }

    private String createSessionCookie()
    {
        if ( getSessionId() == null )
        {
            return null;
        }

        return SESSION_COOKIE_NAME + "=" + getSessionId() + ";";
    }

    /**
     * Write the request body.
     */
    private void writeRequestBody( HttpURLConnection conn, ByteArrayOutputStream baos )
        throws IOException
    {
        baos.writeTo( conn.getOutputStream() );
    }

    /**
     * Validate the response.
     */
    private void validateResponse( HttpURLConnection conn )
        throws IOException
    {
        if ( conn.getResponseCode() >= 300 )
        {
            throw new ClientException(
                "Did not receive successful HTTP response: status code = " + conn.getResponseCode() + ", status message = [" +
                    conn.getResponseMessage() + "]" );
        }
    }

    /**
     * Check the session information.
     */
    private void checkResponseHeaders( HttpURLConnection conn )
    {
        List<String> responseCookieHeaders = conn.getHeaderFields().get( HTTP_HEADER_SET_COOKIE );

        if ( responseCookieHeaders == null )
        {
            return;
        }

        for ( String cookieHeader : responseCookieHeaders )
        {
            if ( cookieHeader == null )
            {
                continue;
            }

            String[] bits = cookieHeader.split( "[=;]" );

            if ( SESSION_COOKIE_NAME.equals( bits[0] ) )
            {
                setSessionId( bits[1] );
            }
            else if ( bits[0] != null )
            {
                getLocalCookies().put( bits[0], bits[1] );
            }
        }
    }

    private Map<String, String> getLocalCookies()
    {
        if ( this.localCookies.get() == null )
        {
            this.localCookies.set( new HashMap<String, String>() );
        }

        return this.localCookies.get();
    }

    /**
     * Read response body.
     */

    private BinRpcInvocationResult readResponseBody( HttpURLConnection conn )
        throws IOException, ClassNotFoundException
    {
        InputStream in = conn.getInputStream();
        ObjectInputStream ois = new ObjectInputStream( in );

        try
        {
            Object obj = ois.readObject();
            if ( !( obj instanceof BinRpcInvocationResult ) )
            {
                throw new ClientException(
                    "Deserialized object needs to be assignable to type [" + BinRpcInvocationResult.class.getName() + "]: " + obj );
            }
            else
            {
                return (BinRpcInvocationResult) obj;
            }
        }
        finally
        {
            ois.close();
        }
    }

    /**
     * Return the stream for invocation.
     */
    private ByteArrayOutputStream getByteArrayOutputStream( BinRpcInvocation invocation )
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );

        try
        {
            oos.writeObject( invocation );
            oos.flush();
        }
        finally
        {
            oos.close();
        }

        return baos;
    }

    /**
     * Return the session id.
     */
    private String getSessionId()
    {
        if ( this.useGlobalSession )
        {
            return this.globalSessionId;
        }
        else
        {
            return this.localSessionIds.get();
        }
    }

    /**
     * Set the session id.
     */
    private void setSessionId( String sessionId )
    {
        if ( this.useGlobalSession )
        {
            this.globalSessionId = sessionId;
        }
        else
        {
            this.localSessionIds.set( sessionId );
        }
    }
}
