package com.enonic.cms.core.search.IndexPerformance;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 11:06 AM
 */
public class IndexQueryMeasureTracker
{

    Map<String, List<Long>> sourceMeasures = Maps.newHashMap();

    //List<Long> measureOld = new ArrayList<Long>();

    //List<Long> measureNew = new ArrayList<Long>();

    protected void addMeasure( String sourceName, Long timeUsed )
    {

        List<Long> measures = sourceMeasures.get( sourceName );

        if ( measures == null )
        {
            measures = new ArrayList<Long>();
            sourceMeasures.put( sourceName, measures );
        }
        measures.add( timeUsed );
    }

    public void printStats( PrintStream out )
    {
        // TODO: Fix this using list of actual measures instead

        final IndexQuerySourceStats oldList = createStatsForMeasures( "Hibernate", sourceMeasures.get( "Hibernate" ) );
        final IndexQuerySourceStats newList = createStatsForMeasures( "ElasticSearch", sourceMeasures.get( "ElasticSearch" ) );

        final String format = "%-10s %-10s %-10s\n";
        out.printf( format, "Stat:", "DB:", "ElasticSearch:" );
        out.printf( format, "--", "--", "--" );
        out.printf( format, "Total#", oldList.totalHits, newList.totalHits );
        out.printf( format, "Min", oldList.minTime, newList.minTime );
        out.printf( format, "Max", oldList.maxTime, newList.maxTime );
        out.printf( format, "Avg", oldList.avgTime, newList.avgTime );
    }


    public List<IndexQuerySourceStats> createStatsForAllSources()
    {

        List<IndexQuerySourceStats> allSourcesStats = Lists.newArrayList();

        for ( String sourceName : sourceMeasures.keySet() )
        {
            allSourcesStats.add( createStatsForMeasures( sourceName, sourceMeasures.get( sourceName ) ) );
        }

        return allSourcesStats;
    }


    private IndexQuerySourceStats createStatsForMeasures( String sourceName, final List<Long> measures )
    {
        IndexQuerySourceStats indexQueryMeasureSourceStats = new IndexQuerySourceStats( sourceName );

        long sum = 0;
        long maxTime = 0;
        long minTime = Long.MAX_VALUE;

        for ( Long measure : measures )
        {
            if ( measure > maxTime )
            {
                maxTime = measure;
            }

            if ( measure < minTime )
            {
                minTime = measure;
            }

            sum += measure;
        }

        if ( measures.size() > 0 )
        {
            indexQueryMeasureSourceStats.totalHits = measures.size();
            indexQueryMeasureSourceStats.avgTime = sum / measures.size();
            indexQueryMeasureSourceStats.maxTime = maxTime;
            indexQueryMeasureSourceStats.minTime = minTime;
        }
        else
        {
            indexQueryMeasureSourceStats.totalHits = -1;
            indexQueryMeasureSourceStats.avgTime = -1;
            indexQueryMeasureSourceStats.maxTime = -1;
            indexQueryMeasureSourceStats.minTime = -1;
        }

        return indexQueryMeasureSourceStats;
    }

}




