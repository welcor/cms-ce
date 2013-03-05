/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.enonic.cms.core.PathAndParamsToStringBuilder;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.structure.SiteEntity;

/**
 * Oct 6, 2010
 */
public final class PortalRequestTrace
    extends BaseTrace
    implements Trace
{
    private long requestNumber;

    private long completedNumber = -1;

    private HttpRequest httpRequest;

    private RequestMode mode;

    private MaxLengthedString url = new MaxLengthedString();

    private User requester;

    private String siteKey;

    private String siteName;

    private MaxLengthedString siteLocalPathAndParams = new MaxLengthedString();

    private MaxLengthedString responseRedirect = new MaxLengthedString();

    private MaxLengthedString responseForward = new MaxLengthedString();

    private PageRenderingTrace pageRenderingTrace;

    private WindowRenderingTrace windowRenderingTrace;

    private AttachmentRequestTrace attachmentRequestTrace;

    private ImageRequestTrace imageRequestTrace;

    private CacheUsages cacheUsages = new CacheUsages();

    public PortalRequestTrace( long requestNumber, String url )
    {
        this.requestNumber = requestNumber;
        this.url = new MaxLengthedString( url );
    }

    public long getRequestNumber()
    {
        return requestNumber;
    }

    public long getCompletedNumber()
    {
        return completedNumber;
    }

    void setCompletedNumber( long completedNumber )
    {
        this.completedNumber = completedNumber;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUrl()
    {
        return url != null ? url.toString() : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public RequestMode getMode()
    {
        return mode;
    }

    void setMode( RequestMode mode )
    {
        this.mode = mode;
    }

    public String getType()
    {
        if ( pageRenderingTrace != null )
        {
            return "P";
        }
        else if ( windowRenderingTrace != null )
        {
            return "W";
        }
        else if ( attachmentRequestTrace != null )
        {
            return "A";
        }
        else if ( imageRequestTrace != null )
        {
            return "I";
        }
        else
        {
            return "?";
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getTypeDescription()
    {
        if ( pageRenderingTrace != null )
        {
            return "Page";
        }
        else if ( windowRenderingTrace != null )
        {
            return "Window";
        }
        else if ( attachmentRequestTrace != null )
        {
            return "Attachment";
        }
        else if ( imageRequestTrace != null )
        {
            return "Image";
        }
        else
        {
            return "Unknown";
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public HttpRequest getHttpRequest()
    {
        return httpRequest;
    }

    void setHttpRequest( HttpRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSiteKey()
    {
        return siteKey;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSiteName()
    {
        return siteName;
    }

    void setSite( SiteEntity site )
    {
        this.siteKey = site.getKey().toString();
        this.siteName = site.getName();
    }

    void setSitePath( SitePath sitePath )
    {
        PathAndParamsToStringBuilder stringBuilder = new PathAndParamsToStringBuilder();
        this.siteLocalPathAndParams = new MaxLengthedString( stringBuilder.toString( sitePath.getPathAndParams() ) );
    }

    public String getSiteLocalUrl()
    {
        return siteLocalPathAndParams != null ? siteLocalPathAndParams.toString() : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public User getRequester()
    {
        return requester;
    }

    void setRequester( User requester )
    {
        this.requester = requester;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getResponseRedirect()
    {
        return responseRedirect != null ? responseRedirect.toString() : null;
    }

    void setResponseRedirect( String responseRedirect )
    {
        this.responseRedirect = new MaxLengthedString( responseRedirect );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getResponseForward()
    {
        return responseForward != null ? responseForward.toString() : null;
    }

    void setResponseForward( String responseForward )
    {
        this.responseForward = new MaxLengthedString( responseForward );
    }

    public boolean hasPageRenderingTrace()
    {
        return pageRenderingTrace != null;
    }

    public PageRenderingTrace getPageRenderingTrace()
    {
        return pageRenderingTrace;
    }

    void setPageRenderingTrace( PageRenderingTrace pageRenderingTrace )
    {
        this.pageRenderingTrace = pageRenderingTrace;
    }

    void setWindowRenderingTrace( WindowRenderingTrace windowRenderingTrace )
    {
        this.windowRenderingTrace = windowRenderingTrace;
    }

    public boolean hasWindowRenderingTrace()
    {
        return windowRenderingTrace != null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public WindowRenderingTrace getWindowRenderingTrace()
    {
        return windowRenderingTrace;
    }

    public boolean hasAttachmentRequsetTrace()
    {
        return attachmentRequestTrace != null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public AttachmentRequestTrace getAttachmentRequestTrace()
    {
        return attachmentRequestTrace;
    }

    void setAttachmentRequestTrace( AttachmentRequestTrace attachmentRequestTrace )
    {
        this.attachmentRequestTrace = attachmentRequestTrace;
    }

    public boolean hasImageRequestTrace()
    {
        return imageRequestTrace != null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ImageRequestTrace getImageRequestTrace()
    {
        return imageRequestTrace;
    }

    void setImageRequestTrace( ImageRequestTrace imageRequestTrace )
    {
        this.imageRequestTrace = imageRequestTrace;
    }

    @SuppressWarnings("UnusedDeclaration")
    public CacheUsages getCacheUsages()
    {
        return cacheUsages;
    }

    void postProcess()
    {
        if ( imageRequestTrace != null )
        {
            cacheUsages.add( imageRequestTrace.getCacheUsage() );
        }
        else if ( windowRenderingTrace != null )
        {
            cacheUsages.add( windowRenderingTrace.getCacheUsage() );
        }
        else if ( pageRenderingTrace != null )
        {
            cacheUsages.add( PageCacheUsagesResolver.resolveCacheUsages( pageRenderingTrace ) );
        }
        else
        {
            cacheUsages = new CacheUsages();
        }
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        PortalRequestTrace that = (PortalRequestTrace) o;

        if ( requestNumber != that.requestNumber )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return (int) ( requestNumber ^ ( requestNumber >>> 32 ) );
    }

    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder( this );
        builder.append( "url", url );
        builder.append( "duration", getDuration() );
        builder.append( "requester", requester );
        return builder.toString();
    }

}
