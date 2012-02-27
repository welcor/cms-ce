package com.enonic.cms.core.search.IndexPerformance;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 11:10 AM
 */
public class IndexQueryStatsCreator
{

    public static List<IndexQueryStats> createIndexQueryStats( Map<String, IndexQueryMeasureTracker> measuresMap )
    {

        List<IndexQueryStats> statsList = Lists.newArrayList();

        for ( String query : measuresMap.keySet() )
        {
            final IndexQueryMeasureTracker indexQueryMeasureTracker = measuresMap.get( query );

            IndexQueryStats singleQueryStats = new IndexQueryStats( query );
            singleQueryStats.setSourceStats( indexQueryMeasureTracker.createStatsForAllSources() );

            statsList.add( singleQueryStats );

        }

        return statsList;
    }


}
