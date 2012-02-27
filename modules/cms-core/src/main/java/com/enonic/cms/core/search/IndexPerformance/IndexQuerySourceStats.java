package com.enonic.cms.core.search.IndexPerformance;

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

    int totalHits = 0;

    long avgTime = -1;

    long maxTime = -1;

    long minTime = -1;

    String sourceName;

    public IndexQuerySourceStats( String sourceName )
    {
        this.sourceName = sourceName;
    }

    public int getTotalHits()
    {
        return totalHits;
    }

    public long getAvgTime()
    {
        return avgTime;
    }

    public long getMaxTime()
    {
        return maxTime;
    }

    public long getMinTime()
    {
        return minTime;
    }

    public String getSourceName()
    {
        return sourceName;
    }
}
