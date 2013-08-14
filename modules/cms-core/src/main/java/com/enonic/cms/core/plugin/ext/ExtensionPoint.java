package com.enonic.cms.core.plugin.ext;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.ExtensionBase;

public abstract class ExtensionPoint<T extends Extension>
    extends FilteredExtensionListener<T>
    implements Iterable<T>, Comparator<T>
{
    private ImmutableList<T> list;

    public ExtensionPoint( final Class<T> type )
    {
        super( type );
        this.list = ImmutableList.of();
    }

    public final String getName()
    {
        return getType().getSimpleName();
    }

    public final boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this.list.iterator();
    }

    protected synchronized final void addExtension( final T ext )
    {
        final List<T> other = Lists.newArrayList( this.list );
        other.add( ext );
        Collections.sort( other, this );
        this.list = ImmutableList.copyOf( other );
    }

    protected synchronized final void removeExtension( final T ext )
    {
        final List<T> other = Lists.newArrayList( this.list );
        other.remove( ext );
        Collections.sort( other, this );
        this.list = ImmutableList.copyOf( other );
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
