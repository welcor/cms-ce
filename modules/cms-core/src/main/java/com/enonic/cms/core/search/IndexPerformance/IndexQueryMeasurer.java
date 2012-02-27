package com.enonic.cms.core.search.IndexPerformance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StopWatch;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/17/12
 * Time: 2:09 PM
 */
public class IndexQueryMeasurer
{
    private final static int FLUSH_TRESHOLD = 3000;

    private Map<String, IndexQueryMeasureTracker> measuresMap = new HashMap<String, IndexQueryMeasureTracker>();

    private IndexQueryMeasureTracker total = new IndexQueryMeasureTracker();

    final String LS = System.getProperty( "line.separator" );

    private int totalSize = 0;


    public List<IndexQueryStats> createStatsSnapshot()
    {
        final List<IndexQueryStats> indexQueryStats = IndexQueryStatsCreator.createIndexQueryStats( measuresMap );

        return indexQueryStats;
    }

    public synchronized void addMeasure( ContentIndexQuery query, StopWatch stopWatch, String sourceName )
    {
        totalSize++;

        if ( totalSize > FLUSH_TRESHOLD )
        {
            flush();
        }

        final long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();

        total.addMeasure( sourceName, lastTaskTimeMillis );

        final String queryString = QueryIdResolver.queryToKey( query );

        if ( !measuresMap.containsKey( queryString ) )
        {
            IndexQueryMeasureTracker indexQueryMeasureTracker = new IndexQueryMeasureTracker();
            measuresMap.put( queryString, indexQueryMeasureTracker );
        }

        final IndexQueryMeasureTracker indexQueryMeasureTracker = measuresMap.get( queryString );
        indexQueryMeasureTracker.addMeasure( sourceName, lastTaskTimeMillis );
    }


    public synchronized void flush()
    {
        File f = new File( "query-log" + Calendar.getInstance().getTime() + ".log" );

        print( System.out );

        writeToFile( f );

        measuresMap = new HashMap<String, IndexQueryMeasureTracker>();
        totalSize = 0;
    }

    private void writeToFile( File f )
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream( baos );
            print( ps );
            String content = baos.toString( "UTF-8" );
            FileUtils.write( f, content, "UTF-8" );
        }
        catch ( IOException e )
        {
            System.out.println( "Failed to write queryMeasures to disk " );
        }
    }

    public void print( PrintStream out )
    {
        for ( String query : measuresMap.keySet() )
        {

            out.println();
            out.println( "-------------------" );
            out.println( "Query: " );
            out.println( "-------" );
            out.println( query );
            out.println();

            final IndexQueryMeasureTracker indexQueryMeasureTracker = measuresMap.get( query );
            indexQueryMeasureTracker.printStats( out );

        }

        out.println( "-------------------" );
        out.println( "Grand total: " );
        total.printStats( out );
    }


}
