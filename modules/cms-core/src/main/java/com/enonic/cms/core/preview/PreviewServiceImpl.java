/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

@Component("previewService")
class PreviewServiceImpl
    implements PreviewService
{
    private static final String PREVIEW_CONTEXT_ATTRIBUTE = "_preview-context";

    public boolean isInPreview()
    {
        HttpServletRequest request = doGetRequest();

        return request != null && "true".equals( request.getAttribute( Attribute.PREVIEW_ENABLED ) );
    }

    public PreviewContext getPreviewContext()
    {
        if ( !isInPreview() )
        {
            return PreviewContext.NO_PREVIEW;
        }

        return doGetPreviewContext();
    }

    public void setPreviewContext( PreviewContext previewContext )
    {
        HttpSession session = doGetSession();

        if ( session != null )
        {
            if ( previewContext.isPreviewingContent() )
            {
                NoLazyInitializationEnforcerForPreview.enforceNoLazyInitialization(
                    previewContext.getContentPreviewContext().getContentPreviewed() );
            }

            session.setAttribute( PREVIEW_CONTEXT_ATTRIBUTE, previewContext );
        }
    }

    private PreviewContext doGetPreviewContext()
    {
        HttpSession session = doGetSession();

        if ( session == null )
        {
            return null;
        }

        return (PreviewContext) session.getAttribute( PREVIEW_CONTEXT_ATTRIBUTE );
    }

    private HttpSession doGetSession()
    {
        final HttpServletRequest servletRequest = doGetRequest();

        if ( servletRequest == null )
        {
            return null;
        }

        return servletRequest.getSession( false );
    }

    private HttpServletRequest doGetRequest()
    {
        return ServletRequestAccessor.getRequest();
    }
}
