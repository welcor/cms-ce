/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.user.UserEntity;

public class DataSourceContext
{
    private SiteKey siteKey = null;

    private PortalInstanceKey portalInstanceKey;

    private UserEntity user;

    private PreviewContext previewContext;

    private HttpServletRequest httpRequest;

    private VerticalSession verticalSession;

    public DataSourceContext( PreviewContext previewContext )
    {
        Preconditions.checkNotNull( previewContext );

        this.previewContext = previewContext;
    }

    public DataSourceContext()
    {
        this.previewContext = PreviewContext.NO_PREVIEW;
    }

    public final void setSiteKey( final SiteKey value )
    {
        siteKey = value;
    }

    public final void setPortalInstanceKey( final PortalInstanceKey value )
    {
        portalInstanceKey = value;
    }

    public final void setPreviewContext( final PreviewContext previewContext )
    {
        this.previewContext = previewContext;
    }

    public final void setUser( final UserEntity user )
    {
        this.user = user;
    }

    public VerticalSession getVerticalSession()
    {
        return verticalSession;
    }

    public HttpServletRequest getHttpRequest()
    {
        return httpRequest;
    }

    public final SiteKey getSiteKey()
    {
        return siteKey;
    }

    public final PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public final UserEntity getUser()
    {
        return user;
    }

    public final PreviewContext getPreviewContext()
    {
        return previewContext;
    }

    public void setHttpRequest( final HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public void setVerticalSession( final VerticalSession verticalSession )
    {
        this.verticalSession = verticalSession;
    }
}
