/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SitePath;

public final class PortalWebContext
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

    public SiteKey getSiteKey()
    {
        return sitePath.getSiteKey();
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

    public boolean isAlreadyProcessingException()
    {
        final Integer isProcessingExceptionCount = (Integer) request.getAttribute( Attribute.PROCESSING_EXCEPTION_COUNT );

        return isProcessingExceptionCount != null;
    }

    public int processingExceptionCount()
    {
        final Integer count = (Integer) request.getAttribute( Attribute.PROCESSING_EXCEPTION_COUNT );
        if ( count == null )
        {
            return 0;
        }
        return count;
    }

    public void increaseProcessingExceptionCount()
    {
        if ( request.getAttribute( Attribute.PROCESSING_EXCEPTION_COUNT ) != null )
        {
            Integer count = (Integer) request.getAttribute( Attribute.PROCESSING_EXCEPTION_COUNT );
            request.setAttribute( Attribute.PROCESSING_EXCEPTION_COUNT, ++count );
        }
        else
        {
            request.setAttribute( Attribute.PROCESSING_EXCEPTION_COUNT, 1 );
        }
    }

}
