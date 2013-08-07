package com.enonic.cms.core.plugin.ext;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.cms.api.plugin.ext.http.HttpProcessor;

public abstract class HttpProcessorExtensions<T extends HttpProcessor>
    extends ExtensionPoint<T>
{
    public HttpProcessorExtensions( final Class<T> type )
    {
        super( type );
    }

    public final List<T> findMatching( final String path )
    {
        final List<T> list = Lists.newArrayList();
        for ( final T ext : this )
        {
            if ( ext.matchesUrlPattern( path ) )
            {
                list.add( ext );
            }
        }

        return list;
    }

    public T findFirstMatching( final String path )
    {
        for ( final T ext : this )
        {
            if ( ext.matchesUrlPattern( path ) )
            {
                return ext;
            }
        }

        return null;
    }

    @Override
    protected final String toHtml( final T ext )
    {
        return composeHtml( ext, "priority", ext.getPriority(), "urlPatterns", Joiner.on( ", " ).skipNulls().join( ext.getUrlPatterns() ) );
    }

    @Override
    public final int compare( final T o1, final T o2 )
    {
        return o1.compareTo( o2 );
    }
}
