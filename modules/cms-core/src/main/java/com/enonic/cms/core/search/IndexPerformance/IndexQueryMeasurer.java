package com.enonic.cms.core.search.IndexPerformance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StopWatch;

import com.google.common.collect.Maps;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/17/12
 * Time: 2:09 PM
 */
public class IndexQueryMeasurer
{
    Map<IndexQuerySignature, IndexQueryMeasure> queryMeasures = Maps.newHashMap();

    public synchronized void addMeasure( ContentIndexQuery query, StopWatch stopWatch, String sourceName )
    {
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

}
