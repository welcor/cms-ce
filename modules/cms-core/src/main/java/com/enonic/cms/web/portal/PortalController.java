package com.enonic.cms.web.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.web.portal.attachment.AttachmentHandler;
import com.enonic.cms.web.portal.captcha.CaptchaHandler;
import com.enonic.cms.web.portal.handler.WebContext;
import com.enonic.cms.web.portal.handler.WebHandler;
import com.enonic.cms.web.portal.handler.WebHandlerSet;
import com.enonic.cms.web.portal.image.ImageHandler;
import com.enonic.cms.web.portal.render.DefaultHandler;
import com.enonic.cms.web.portal.resource.ResourceHandler;

@Controller
public final class PortalController
{
    private final WebHandlerSet handlerSet;

    public PortalController()
    {
        this.handlerSet = new WebHandlerSet();
    }

    @RequestMapping("/{site}/**")
    public void handleRequest( @PathVariable("site") final int siteKey, final HttpServletRequest request,
                               final HttpServletResponse response )
        throws Exception
    {
        final WebContext context = new WebContext();
        context.setRequest( request );
        context.setResponse( response );
        context.setSitePath( resolveSitePath( new SiteKey( siteKey ), request ) );

        final WebHandler handler = this.handlerSet.find( context );
        handler.handle( context );
    }

    @SuppressWarnings("unchecked")
    private SitePath resolveSitePath( final SiteKey siteKey, final HttpServletRequest request )
    {
        final String path = request.getRequestURI();
        final Iterable<String> elements = Splitter.on( '/' ).omitEmptyStrings().split( path );

        final Iterable<String> localElements = Iterables.skip( elements, 2 );

        String localPath = Joiner.on( '/' ).join( localElements );
        if (path.endsWith( "/" )) {
            localPath = localPath + "/";
        }

        return new SitePath( siteKey, localPath, request.getParameterMap() );
    }

    @Autowired
    public void setCaptchaHandler( final CaptchaHandler handler )
    {
        this.handlerSet.addHandler( handler );
    }

    @Autowired
    public void setImageHandler( final ImageHandler handler )
    {
        this.handlerSet.addHandler( handler );
    }

    @Autowired
    public void setResourceHandler( final ResourceHandler handler )
    {
        this.handlerSet.addHandler( handler );
    }

    @Autowired
    public void setAttachmentHandler( final AttachmentHandler handler )
    {
        this.handlerSet.addHandler( handler );
    }

    @Autowired
    public void setDefaultHandler( final DefaultHandler handler )
    {
        this.handlerSet.setDefaultHandler( handler );
    }
}
