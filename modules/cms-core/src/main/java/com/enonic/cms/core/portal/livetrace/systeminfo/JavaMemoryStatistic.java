package com.enonic.cms.core.portal.livetrace.systeminfo;


class JavaMemoryStatistic
{
    private long init;

    private long used;

    private long committed;

    private long max;

    @SuppressWarnings("UnusedDeclaration")
    public long getInit()
    {
        return init;
    }

    void setInit( long init )
    {
        this.init = init;
    }

    @SuppressWarnings("UnusedDeclaration")
    public long getUsed()
    {
        return used;
    }

    void setUsed( long used )
    {
        this.used = used;
    }

    @SuppressWarnings("UnusedDeclaration")
    public long getCommitted()
    {
        return committed;
    }

    void setCommitted( long committed )
    {
        this.committed = committed;
    }

    @SuppressWarnings("UnusedDeclaration")
    public long getMax()
    {
        return max;
    }

    void setMax( long max )
    {
        this.max = max;
    }
}
