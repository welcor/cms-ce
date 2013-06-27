/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


public class HttpRequest
{
    private MaxLengthedString remoteAddress = new MaxLengthedString();

    private MaxLengthedString characterEncoding = new MaxLengthedString();

    private MaxLengthedString contentType = new MaxLengthedString();

    private MaxLengthedString userAgent = new MaxLengthedString();

    @SuppressWarnings("UnusedDeclaration")
    public String getRemoteAddress()
    {
        return remoteAddress != null ? remoteAddress.toString() : null;
    }

    void setRemoteAddress( String remoteAddress )
    {
        this.remoteAddress = new MaxLengthedString( remoteAddress );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCharacterEncoding()
    {
        return characterEncoding != null ? characterEncoding.toString() : null;
    }

    void setCharacterEncoding( String characterEncoding )
    {
        this.characterEncoding = new MaxLengthedString( characterEncoding );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentType()
    {
        return contentType != null ? contentType.toString() : null;
    }

    void setContentType( String contentType )
    {
        this.contentType = new MaxLengthedString( contentType );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUserAgent()
    {
        return userAgent != null ? userAgent.toString() : null;
    }

    void setUserAgent( String userAgent )
    {
        this.userAgent = new MaxLengthedString( userAgent );
    }
}
