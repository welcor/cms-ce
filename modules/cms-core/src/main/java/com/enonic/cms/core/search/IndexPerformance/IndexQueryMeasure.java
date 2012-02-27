package com.enonic.cms.core.search.IndexPerformance;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 1:33 PM
 */
public class IndexQueryMeasure
    implements Serializable
{
    IndexQuerySignature querySignature;

    Map<String, IndexQuerySourceStats> sourceStats = Maps.newHashMap();

    private long totalTime = 0;

    private long numberOfInvocations = 0;

    public IndexQueryMeasure( IndexQuerySignature querySignature )
    {
        this.querySignature = querySignature;
    }

    public void addMeasure( String sourceName, long executionTime )
    {
        IndexQuerySourceStats indexQuerySourceStats = sourceStats.get( sourceName );

        if ( indexQuerySourceStats == null )
        {
            indexQuerySourceStats = new IndexQuerySourceStats();
            sourceStats.put( sourceName, indexQuerySourceStats );
        }

        indexQuerySourceStats.updateStats( executionTime );
    }

    public IndexQuerySignature getQuerySignature()
    {
        return querySignature;
    }

    public Map<String, IndexQuerySourceStats> getSourceStats()
    {
        return sourceStats;
    }

    public long getQueryAverageExecutionTime()
    {
        return numberOfInvocations > 0 ? totalTime / numberOfInvocations : -1;
    }

    public long getNumberOfInvocations()
    {
        return numberOfInvocations;
    }
}
