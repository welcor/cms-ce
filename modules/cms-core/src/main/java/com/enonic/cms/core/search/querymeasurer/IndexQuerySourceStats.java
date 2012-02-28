package com.enonic.cms.core.search.querymeasurer;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 10:27 AM
 */
public class IndexQuerySourceStats
    implements Serializable
{
    private int totalHits = 0;

    private long maxTime = -1;

    private long minTime = -1;

    private long totalTime = -1;

    public int getTotalHits()
    {
        return totalHits;
    }

    public long getAvgTime()
    {
        return totalHits > 0 ? totalTime / totalHits : -1;
    }

    public long getMaxTime()
    {
        return maxTime;
    }

    public long getMinTime()
    {
        return minTime;
    }

    public void updateStats( long newMeasure )
    {
        totalHits++;

        totalTime += newMeasure;

        if ( newMeasure > maxTime )
        {
            maxTime = newMeasure;
        }

        if ( minTime < 0 || newMeasure < minTime )
        {
            minTime = newMeasure;
        }
    }

    public long getTotalTime()
    {
        return totalTime;
    }
}
