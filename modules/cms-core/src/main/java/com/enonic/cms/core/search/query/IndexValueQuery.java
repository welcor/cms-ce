/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query;

import com.enonic.cms.core.search.ContentIndexServiceImpl;

/**
 * This class implements the index value query.
 */
public final class IndexValueQuery
    extends AbstractQuery
{
    /**
     * Path of index value.
     */
    private final String field;

    private int index = 0;

    private int count = ContentIndexServiceImpl.COUNT_OPTIMIZER_THRESHOULD_VALUE;

    private boolean returnAllHits = false;

    private boolean descOrder;

    public IndexValueQuery( String field )
    {
        this.field = field;
    }

    public String getField()
    {
        return this.field;
    }

    public int getIndex()
    {
        return this.index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public int getCount()
    {
        return this.count;
    }

    public void setCount( int count )
    {
        this.count = count;
    }

    public boolean isDescOrder()
    {
        return this.descOrder;
    }

    public void setDescOrder( boolean descOrder )
    {
        this.descOrder = descOrder;
    }

    public boolean doReturnAllHits()
    {
        return returnAllHits;
    }

    public void setReturnAllHits( final boolean returnAllHits )
    {
        this.returnAllHits = returnAllHits;
    }
}
