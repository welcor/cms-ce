package com.enonic.cms.web.portal.handler;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.web.portal.PortalWebContext;

@Component
public final class WebHandlerRegistryImpl
    implements WebHandlerRegistry
{
    private List<WebHandler> list;

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

        throw new IllegalArgumentException( "Could not find handler for " + context.getSitePath() );
    }

    @Autowired
    public void setHandlers( final WebHandler... list )
    {
        this.list = Lists.newArrayList( list );
        Collections.sort( this.list, new AnnotationAwareOrderComparator() );
    }
}
