package com.enonic.cms.core.content;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class ContentMap
    implements Iterable<ContentEntity>
{
    private final List<ContentKey> orderMask;

    private LinkedHashMap<ContentKey, ContentEntity> insertionOrderedMapByKey;

    public ContentMap( final List<ContentKey> contentOrderMask )
    {
        Preconditions.checkNotNull( contentOrderMask, "contentOrderMask must be given" );

        this.orderMask = contentOrderMask;

        insertionOrderedMapByKey = new LinkedHashMap<ContentKey, ContentEntity>( contentOrderMask.size() );
        for ( ContentKey contentKey : contentOrderMask )
        {
            insertionOrderedMapByKey.put( contentKey, null );
        }
    }

    public void addAll( final Iterable<ContentEntity> it )
    {
        for ( ContentEntity c : it )
        {
            if ( !insertionOrderedMapByKey.containsKey( c.getKey() ) )
            {
                throw new IllegalStateException( "Trying to add content that does not exist in order mask" );
            }
            insertionOrderedMapByKey.put( c.getKey(), c );
        }
    }

    public void add( final ContentEntity content )
    {
        if ( !insertionOrderedMapByKey.containsKey( content.getKey() ) )
        {
            throw new IllegalStateException( "Trying to add content that does not exist in order mask" );
        }
        insertionOrderedMapByKey.put( content.getKey(), content );
    }

    public void removeEntriesWithNullValues()
    {
        final List<ContentKey> keysToRemove = Lists.newArrayList();
        for ( Map.Entry<ContentKey, ContentEntity> entry : insertionOrderedMapByKey.entrySet() )
        {
            if ( entry.getValue() == null )
            {
                keysToRemove.add( entry.getKey() );
            }
        }
        for ( ContentKey keyToRemove : keysToRemove )
        {
            insertionOrderedMapByKey.remove( keyToRemove );
        }
    }


    public ContentEntity get( final ContentKey key )
    {
        return insertionOrderedMapByKey.get( key );
    }

    @Override
    public Iterator<ContentEntity> iterator()
    {
        return insertionOrderedMapByKey.values().iterator();
    }

    public Collection<ContentEntity> collection()
    {
        return insertionOrderedMapByKey.values();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ContentMap that = (ContentMap) o;

        return Objects.equal( orderMask, that.orderMask ) && Objects.equal( insertionOrderedMapByKey, that.insertionOrderedMapByKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( orderMask, insertionOrderedMapByKey );
    }
}
