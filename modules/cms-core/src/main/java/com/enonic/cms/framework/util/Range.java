package com.enonic.cms.framework.util;

public class Range
{
    private final long start;
    private final long end;
    private final long total;
    private final long length;

    public Range( final long start, final long end, final long total )
    {
        this.start = start;
        this.end = end;
        this.total = total;
        this.length = end - start + 1;
    }

    public boolean isRoot( long length )
    {
        return start == 0 && end == ( length - 1 ) && total == length;
    }

    public long getStart()
    {
        return start;
    }

    public long getEnd()
    {
        return end;
    }

    public long getTotal()
    {
        return total;
    }

    public long getLength()
    {
        return length;
    }
}
