package com.enonic.cms.core.plugin.ext;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.ExtensionBase;

public abstract class ExtensionPoint<T extends Extension>
    extends FilteredExtensionListener<T>
    implements Iterable<T>, Comparator<T>
{
    private final Set<T> set;

    public ExtensionPoint( final Class<T> type )
    {
        super( type );
        this.set = Sets.newTreeSet( this );
    }

    public final String getName()
    {
        return getType().getSimpleName();
    }

    public final boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this.set.iterator();
    }

    protected final void addExtension( final T ext )
    {
        this.set.add( ext );
    }

    protected final void removeExtension( final T ext )
    {
        this.set.remove( ext );
    }

    public final List<String> toHtml()
    {
        final List<String> list = Lists.newArrayList();
        for ( final T ext : this )
        {
            list.add( toHtml( ext ) );
        }

        return list;
    }

    protected abstract String toHtml( T ext );

    protected static String composeHtml( final Extension ext, final Object... props )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "<span title=\"" ).append( ext.getClass().getName() ).append( "\"><strong>" );
        str.append( composeDisplayName( ext ) );
        str.append( "</strong></span>" );

        if ( props != null )
        {
            str.append( "<ul>" );

            for ( int i = 0; i < props.length; i += 2 )
            {
                final String key = String.valueOf( props[i] );
                final String value = ( i < ( props.length - 1 ) ) ? String.valueOf( props[i + 1] ) : "";
                str.append( "<li>" ).append( key ).append( " : " ).append( value ).append( "</li>" );
            }

            str.append( "</ul>" );
        }

        return str.toString();
    }

    private static String composeDisplayName( final Extension extension )
    {
        if ( extension instanceof ExtensionBase )
        {
            return ( (ExtensionBase) extension ).getDisplayName();
        }

        return extension.getClass().getSimpleName();
    }
}
