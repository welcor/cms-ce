package com.enonic.cms.core.portal.livetrace.systeminfo;


class JavaThreadStatistic
{
    private int count;

    private int peakCount;

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
    public int getPeakCount()
    {
        return peakCount;
    }

    void setPeakCount( int peakCount )
    {
        this.peakCount = peakCount;
    }
}
