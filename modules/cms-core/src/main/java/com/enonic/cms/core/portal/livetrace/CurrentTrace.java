/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

public class CurrentTrace
{
    private PortalRequestTrace portalRequestTrace;

    private ImageRequestTrace imageRequestTrace;

    private PageRenderingTrace pageRenderingTrace;

    private DatasourceExecutionTrace datasourceExecutionTrace;

    private ClientMethodExecutionTrace clientMethodExecutionTrace;

    private ViewTransformationTrace pageViewTransformationTrace;

    private ViewTransformationTrace windowViewTransformationTrace;

    private ViewFunctionTrace viewFunctionTrace;

    private WindowRenderingTrace windowRenderingTrace;

    PortalRequestTrace getPortalRequestTrace()
    {
        return portalRequestTrace;
    }

    void setPortalRequestTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
    }

    public ImageRequestTrace getImageRequestTrace()
    {
        return imageRequestTrace;
    }

    void setImageRequestTrace( ImageRequestTrace imageRequestTrace )
    {
        this.imageRequestTrace = imageRequestTrace;
    }

    PageRenderingTrace getPageRenderingTrace()
    {
        return pageRenderingTrace;
    }

    void setPageRenderingTrace( PageRenderingTrace pageRenderingTrace )
    {
        this.pageRenderingTrace = pageRenderingTrace;
    }

    DatasourceExecutionTrace getDatasourceExecutionTrace()
    {
        return datasourceExecutionTrace;
    }

    void setDatasourceExecutionTrace( DatasourceExecutionTrace datasourceExecutionTrace )
    {
        this.datasourceExecutionTrace = datasourceExecutionTrace;
    }

    ClientMethodExecutionTrace getClientMethodExecutionTrace()
    {
        return clientMethodExecutionTrace;
    }

    void setClientMethodExecutionTrace( ClientMethodExecutionTrace clientMethodExecutionTrace )
    {
        this.clientMethodExecutionTrace = clientMethodExecutionTrace;
    }

    ViewTransformationTrace getPageViewTransformationTrace()
    {
        return pageViewTransformationTrace;
    }

    void setPageViewTransformationTrace( ViewTransformationTrace pageViewTransformationTrace )
    {
        this.pageViewTransformationTrace = pageViewTransformationTrace;
    }

    ViewTransformationTrace getWindowViewTransformationTrace()
    {
        return windowViewTransformationTrace;
    }

    void setWindowViewTransformationTrace( ViewTransformationTrace windowViewTransformationTrace )
    {
        this.windowViewTransformationTrace = windowViewTransformationTrace;
    }

    public void removeCurrentViewTransformationTrace()
    {
        if ( windowViewTransformationTrace != null )
        {
            windowViewTransformationTrace = null;
        }
        else
        {
            pageViewTransformationTrace = null;
        }
    }

    ViewFunctionTrace getViewFunctionTrace()
    {
        return viewFunctionTrace;
    }

    void setViewFunctionTrace( ViewFunctionTrace viewFunctionTrace )
    {
        this.viewFunctionTrace = viewFunctionTrace;
    }

    WindowRenderingTrace getWindowRenderingTrace()
    {
        return windowRenderingTrace;
    }

    void setWindowRenderingTrace( WindowRenderingTrace windowRenderingTrace )
    {
        this.windowRenderingTrace = windowRenderingTrace;
    }

    boolean isInPageRenderingTrace()
    {
        return windowRenderingTrace == null && pageRenderingTrace != null;
    }

    public ContentIndexQuerier getCurrentContentIndexQuerier()
    {
        if ( clientMethodExecutionTrace != null )
        {
            return clientMethodExecutionTrace;
        }
        return datasourceExecutionTrace;
    }

    public RelatedContentFetcher getCurrentRelatedContentFetcher()
    {
        if ( clientMethodExecutionTrace != null )
        {
            return clientMethodExecutionTrace;
        }
        return datasourceExecutionTrace;
    }
}
