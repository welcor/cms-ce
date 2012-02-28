package com.enonic.cms.core.search.querymeasurer;

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

    private int totalTime = 0;

    private int numberOfInvocations = 0;

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

        numberOfInvocations++;
        totalTime += executionTime;
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

    public int getNumberOfInvocations()
    {
        return numberOfInvocations;
    }


    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        IndexQueryMeasure that = (IndexQueryMeasure) o;

        if ( numberOfInvocations != that.numberOfInvocations )
        {
            return false;
        }
        if ( totalTime != that.totalTime )
        {
            return false;
        }
        if ( querySignature != null ? !querySignature.equals( that.querySignature ) : that.querySignature != null )
        {
            return false;
        }
        if ( sourceStats != null ? !sourceStats.equals( that.sourceStats ) : that.sourceStats != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = querySignature != null ? querySignature.hashCode() : 0;
        result = 31 * result + ( sourceStats != null ? sourceStats.hashCode() : 0 );
        result = 31 * result + totalTime;
        result = 31 * result + numberOfInvocations;
        return result;
    }

    public int getHighestAvgTime()
    {
        long maxFoundValue = 0;

        for ( String source : sourceStats.keySet() )
        {
            final long foundAvgTime = sourceStats.get( source ).getAvgTime();

            if ( foundAvgTime > maxFoundValue )
            {
                maxFoundValue = foundAvgTime;
            }

        }

        return Long.valueOf( maxFoundValue ).intValue();
    }


    public int getAvgTimeDiff()
    {
        long maxFoundValue = 0;
        long minFoundValue = Integer.MAX_VALUE;

        for ( String source : sourceStats.keySet() )
        {
            final long foundAvgTime = sourceStats.get( source ).getAvgTime();

            if ( foundAvgTime > maxFoundValue )
            {
                maxFoundValue = foundAvgTime;
            }

            if ( foundAvgTime < minFoundValue )
            {
                minFoundValue = foundAvgTime;
            }
        }

        return Long.valueOf( maxFoundValue - minFoundValue ).intValue();
    }

}
