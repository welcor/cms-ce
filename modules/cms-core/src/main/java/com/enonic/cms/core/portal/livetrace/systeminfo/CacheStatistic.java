package com.enonic.cms.core.portal.livetrace.systeminfo;


class CacheStatistic
{
    private int count;

    private int capacity;

    private int hitCount;

    private int missCount;

    @SuppressWarnings("UnusedDeclaration")
    public int getCount()
    {
        return count;
    }

    void setCount( int count )
    {
        this.count = count;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getCapacity()
    {
        return capacity;
    }

    void setCapacity( int capacity )
    {
        this.capacity = capacity;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getHitCount()
    {
        return hitCount;
    }

    void setHitCount( int hitCount )
    {
        this.hitCount = hitCount;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getMissCount()
    {
        return missCount;
    }


    void setMissCount( int missCount )
    {
        this.missCount = missCount;
    }
}
