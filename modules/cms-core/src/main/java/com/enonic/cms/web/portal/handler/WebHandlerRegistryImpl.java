package com.enonic.cms.web.portal.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.attachment.AttachmentHandler;
import com.enonic.cms.web.portal.captcha.CaptchaHandler;
import com.enonic.cms.web.portal.image.ImageHandler;
import com.enonic.cms.web.portal.instanttrace.InstantTraceHandler;
import com.enonic.cms.web.portal.instanttrace.InstantTraceInfoHandler;
import com.enonic.cms.web.portal.instanttrace.InstantTraceResourceHandler;
import com.enonic.cms.web.portal.page.PageHandler;
import com.enonic.cms.web.portal.page.PageRedirectHandler;
import com.enonic.cms.web.portal.resource.ResourceHandler;
import com.enonic.cms.web.portal.services.ServicesHandler;

@Component
public final class WebHandlerRegistryImpl
    implements WebHandlerRegistry
{
    private final List<WebHandler> list;

    private PageHandler defaultHandler;

    public WebHandlerRegistryImpl()
    {
        this.list = Lists.newArrayList();
    }

    @Override
    public WebHandler find( final PortalWebContext context )
    {
        for ( final WebHandler handler : this.list )
        {
            if ( handler.canHandle( context ) )
            {
                return handler;
            }
        }

        return this.defaultHandler;
    }

    @Autowired
    public void setAttachmentHandler( final AttachmentHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setCaptchaHandler( final CaptchaHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setImageHandler( final ImageHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setResourceHandler( final ResourceHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setServicesHandler( final ServicesHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setPageRedirectHandler( final PageRedirectHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setPageHandler( final PageHandler handler )
    {
        this.defaultHandler = handler;
    }

    @Autowired
    public void setInstantTraceHandler( final InstantTraceHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setInstantTraceResourceHandler( final InstantTraceResourceHandler handler )
    {
        this.list.add( handler );
    }

    @Autowired
    public void setInstantTraceInfoHandler( final InstantTraceInfoHandler handler )
    {
        this.list.add( handler );
    }
}
