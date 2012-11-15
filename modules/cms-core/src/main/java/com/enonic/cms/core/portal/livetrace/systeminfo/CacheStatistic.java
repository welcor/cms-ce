package com.enonic.cms.core.portal.livetrace.systeminfo;


class CacheStatistic
{
    private int count;

    private int capacity;

    private long hitCount;

    private long missCount;

    private long removeAllCount;

    private int memoryCapacityUsage;

    private int Effectiveness;

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
    public long getHitCount()
    {
        return hitCount;
    }

    void setHitCount( long hitCount )
    {
        this.hitCount = hitCount;
    }

    @SuppressWarnings("UnusedDeclaration")
    public long getMissCount()
    {
        return missCount;
    }

    void setMissCount( long missCount )
    {
        this.missCount = missCount;
    }

    void setRemoveAllCount( final long removeAllCount )
    {
        this.removeAllCount = removeAllCount;
    }

    @SuppressWarnings("UnusedDeclaration")
    public long getRemoveAllCount()
    {
        return removeAllCount;
    }

    void setMemoryCapacityUsage( final int memoryCapacityUsage )
    {
        this.memoryCapacityUsage = memoryCapacityUsage;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getMemoryCapacityUsage()
    {
        return memoryCapacityUsage;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getEffectiveness()
    {
        return Effectiveness;
    }

    void setEffectiveness( final int effectiveness )
    {
        Effectiveness = effectiveness;
    }
}
