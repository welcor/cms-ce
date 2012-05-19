package com.enonic.cms.web.portal.handler;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.cms.core.SitePath;

public final class WebHandlerSet
{
    private final List<WebHandler> handlers;

    private WebHandler defaultHandler;

    public WebHandlerSet()
    {
        this.handlers = Lists.newArrayList();
    }

    public void addHandler( final WebHandler handler )
    {
        this.handlers.add( handler );
    }

    public void setDefaultHandler( final WebHandler handler )
    {
        this.defaultHandler = handler;
    }

    public WebHandler find( final WebContext context )
    {
        final SitePath sitePath = context.getSitePath();

        for ( final WebHandler handler : this.handlers )
        {
            if ( handler.canHandle( sitePath ) )
            {
                return handler;
            }
        }

        return this.defaultHandler;
    }
}
