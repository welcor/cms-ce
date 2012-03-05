package com.enonic.cms.core.search.querymeasurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.elasticsearch.common.collect.Sets;
import org.springframework.util.StopWatch;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.querymeasurer.comparator.IndexQueryMeasureAvgTimeDiffComparator;
import com.enonic.cms.core.search.querymeasurer.comparator.IndexQueryMeasureInvocationComparator;
import com.enonic.cms.core.search.querymeasurer.comparator.IndexQueryMeasurerAvgTimeComparator;
import com.enonic.cms.core.search.querymeasurer.comparator.IndexQueryMeasurerMaxTimeComparator;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/17/12
 * Time: 2:09 PM
 */
public class IndexQueryMeasurer
{
    private final Map<IndexQuerySignature, IndexQueryMeasure> queryMeasures = Maps.newHashMap();

    private int totalQueriesOnIndex = 0;

    public synchronized void addMeasure( ContentIndexQuery query, StopWatch stopWatch, String sourceName )
    {
        totalQueriesOnIndex++;

        final long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();

        final IndexQuerySignature querySignature = QuerySignatureResolver.createQuerySignature( query );

        IndexQueryMeasure indexQueryMeasure = queryMeasures.get( querySignature );

        if ( indexQueryMeasure == null )
        {
            indexQueryMeasure = new IndexQueryMeasure( querySignature );
            queryMeasures.put( querySignature, indexQueryMeasure );
        }

        indexQueryMeasure.addMeasure( sourceName, lastTaskTimeMillis );
    }

    public Map<IndexQuerySignature, IndexQueryMeasure> getQueryMeasures()
    {
        return queryMeasures;
    }

    public List<IndexQueryMeasure> getAllMeasures()
    {
        return new ArrayList<IndexQueryMeasure>( this.queryMeasures.values() );
    }

    public Set<IndexQueryMeasure> getMeasuresOrderedByTotalExecutions( int count )
    {
        final TreeSet<IndexQueryMeasure> indexQueryMeasures = Sets.newTreeSet( new IndexQueryMeasureInvocationComparator() );
        indexQueryMeasures.addAll( queryMeasures.values() );

        return limitSetIfNeccesary( count, indexQueryMeasures );
    }

    public Set<IndexQueryMeasure> getMeasuresOrderedByAvgDiffTime( int count )
    {
        final TreeSet<IndexQueryMeasure> sortedSet = Sets.newTreeSet( new IndexQueryMeasureAvgTimeDiffComparator() );
        sortedSet.addAll( queryMeasures.values() );

        return limitSetIfNeccesary( count, sortedSet );
    }

    public Set<IndexQueryMeasure> getMeasuresOrderedByAvgTime( int count )
    {
        final TreeSet<IndexQueryMeasure> sortedSet = Sets.newTreeSet( new IndexQueryMeasurerAvgTimeComparator() );
        sortedSet.addAll( queryMeasures.values() );

        return limitSetIfNeccesary( count, sortedSet );
    }

    public Set<IndexQueryMeasure> getMeasuresOrderedByMaxTime( int count )
    {
        final TreeSet<IndexQueryMeasure> sortedSet = Sets.newTreeSet( new IndexQueryMeasurerMaxTimeComparator() );
        sortedSet.addAll( queryMeasures.values() );

        return limitSetIfNeccesary( count, sortedSet );
    }


    private Set<IndexQueryMeasure> limitSetIfNeccesary( int count, TreeSet<IndexQueryMeasure> sortedSet )
    {
        if ( sortedSet.size() > count )
        {
            IndexQueryMeasure lastElement = Iterables.get( sortedSet, count );
            return sortedSet.headSet( lastElement );
        }

        return sortedSet;
    }


    public void clearStatistics()
    {
        queryMeasures.clear();
    }

    public int getTotalQueriesOnIndex()
    {
        return totalQueriesOnIndex;
    }

    public int getRecordedQueries()
    {
        return queryMeasures.size();
    }
}
