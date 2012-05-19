package com.enonic.cms.web.portal.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SitePath;

public final class WebContext
{
    private HttpServletRequest request;

    private HttpServletResponse response;

    private SitePath sitePath;

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public void setRequest( final HttpServletRequest request )
    {
        this.request = request;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }

    public void setResponse( final HttpServletResponse response )
    {
        this.response = response;
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public void setSitePath( final SitePath sitePath )
    {
        this.sitePath = sitePath;
    }

    public SitePath getOriginalSitePath()
    {
        return (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
    }

    public void setOriginalSitePath( final SitePath sitePath )
    {
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );
    }

    public String getOriginalUrl()
    {
        return (String) request.getAttribute( Attribute.ORIGINAL_URL );
    }

    public String getReferrerHeader()
    {
        return request.getHeader( "referer" );
    }
}
