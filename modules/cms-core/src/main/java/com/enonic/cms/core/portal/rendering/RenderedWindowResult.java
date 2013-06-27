/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

/**
 * Apr 23, 2009
 */
public class RenderedWindowResult
    implements Serializable
{
    private String contentEncoding = "UTF-8";

    private String content;

    private String httpContentType;

    private DateTime expirationTimeInCache;

    private String outputMethod;

    private static final Pattern NAMESPACE_PATTERN = Pattern.compile( "xmlns(:\\w+)?=\\\"http://www\\.w3\\.org/1999/xhtml\\\"" );

    public boolean isErrorFree()
    {
        return !( this instanceof ErrorRenderPortletResult );
    }

    public void setContent( String value )
    {
        this.content = value;
    }

    public String getContent()
    {
        return content;
    }

    public String getOutputMethod()
    {
        return outputMethod;
    }

    public void setOutputMethod( String outputMethod )
    {
        this.outputMethod = outputMethod;
    }

    public byte[] getContentAsBytes()
        throws UnsupportedEncodingException
    {
        return content.getBytes( contentEncoding );
    }

    public String getHttpContentType()
    {
        return httpContentType;
    }

    public void setHttpContentType( String httpContentType )
    {
        this.httpContentType = httpContentType;
    }

    public DateTime getExpirationTimeInCache()
    {
        return expirationTimeInCache;
    }

    public void setExpirationTimeInCache( DateTime expirationTimeInCache )
    {
        this.expirationTimeInCache = expirationTimeInCache;
    }

    public void setContentEncoding( String value )
    {
        this.contentEncoding = value;
    }

    public String getContentEncoding()
    {
        return contentEncoding;
    }

    public void stripXHTMLNamespaces()
    {
        if ( content == null )
        {
            return;
        }

        content = stripNamespaces( content );
    }

    private String stripNamespaces( String content )
    {
        final Matcher matcher = NAMESPACE_PATTERN.matcher( content );

        final String result = matcher.replaceAll( "" );

        return result;
    }

    public RenderedWindowResult clone()
    {
        RenderedWindowResult clone = new RenderedWindowResult();
        clone.setContent( content );
        clone.setContentEncoding( contentEncoding );
        clone.setExpirationTimeInCache( expirationTimeInCache );
        clone.setHttpContentType( httpContentType );
        clone.setOutputMethod( outputMethod );

        return clone;
    }


}
