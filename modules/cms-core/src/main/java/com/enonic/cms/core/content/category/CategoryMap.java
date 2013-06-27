/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class CategoryMap
    implements Iterable<CategoryEntity>
{
    private final List<CategoryKey> orderMask;

    private LinkedHashMap<CategoryKey, CategoryEntity> insertionOrderedMapByKey;

    public CategoryMap( final List<CategoryKey> categoryOrderMask )
    {
        Preconditions.checkNotNull( categoryOrderMask, "categoryOrderMask must be given" );

        this.orderMask = categoryOrderMask;

        insertionOrderedMapByKey = new LinkedHashMap<CategoryKey, CategoryEntity>( categoryOrderMask.size() );
        for ( CategoryKey categoryKey : categoryOrderMask )
        {
            insertionOrderedMapByKey.put( categoryKey, null );
        }
    }

    public void addAll( final Iterable<CategoryEntity> it )
    {
        for ( CategoryEntity c : it )
        {
            if ( !insertionOrderedMapByKey.containsKey( c.getKey() ) )
            {
                throw new IllegalStateException( "Trying to add category that does not exist in order mask" );
            }
            insertionOrderedMapByKey.put( c.getKey(), c );
        }
    }

    public void add( final CategoryEntity category )
    {
        if ( !insertionOrderedMapByKey.containsKey( category.getKey() ) )
        {
            throw new IllegalStateException( "Trying to add category that does not exist in order mask" );
        }
        insertionOrderedMapByKey.put( category.getKey(), category );
    }

    public void removeEntriesWithNullValues()
    {
        final List<CategoryKey> keysToRemove = Lists.newArrayList();
        for ( Map.Entry<CategoryKey, CategoryEntity> entry : insertionOrderedMapByKey.entrySet() )
        {
            if ( entry.getValue() == null )
            {
                keysToRemove.add( entry.getKey() );
            }
        }
        for ( CategoryKey keyToRemove : keysToRemove )
        {
            insertionOrderedMapByKey.remove( keyToRemove );
        }
    }


    public CategoryEntity get( final CategoryKey key )
    {
        return insertionOrderedMapByKey.get( key );
    }

    @Override
    public Iterator<CategoryEntity> iterator()
    {
        return insertionOrderedMapByKey.values().iterator();
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

        final CategoryMap that = (CategoryMap) o;

        return Objects.equal( orderMask, that.orderMask ) && Objects.equal( insertionOrderedMapByKey, that.insertionOrderedMapByKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( orderMask, insertionOrderedMapByKey );
    }
}
