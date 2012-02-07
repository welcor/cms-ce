package com.enonic.cms.core.search.IndexPerformance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
public class QueryMeasurer
{
    private final static int FLUSH_TRESHOLD = 10000;

    private Map<String, QueryMeasure> measuresMap = new HashMap<String, QueryMeasure>();

    private QueryMeasure total = new QueryMeasure();

    final String LS = System.getProperty( "line.separator" );

    private int totalSize = 0;

    public synchronized void addMeasure( ContentIndexQuery query, StopWatch stopWatch, boolean oldService )
    {
        totalSize++;

        if ( totalSize > FLUSH_TRESHOLD )
        {
            flush();
        }

        final long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();

        total.addMeasure( oldService, lastTaskTimeMillis );

        final String queryString = QueryIdResolver.queryToKey( query );

        if ( !measuresMap.containsKey( queryString ) )
        {
            QueryMeasure queryMeasure = new QueryMeasure();
            measuresMap.put( queryString, queryMeasure );
        }

        final QueryMeasure queryMeasure = measuresMap.get( queryString );
        queryMeasure.addMeasure( oldService, lastTaskTimeMillis );
    }

    public synchronized void flush()
    {
        File f = new File( "query-log" + Calendar.getInstance().getTime() + ".log" );

        print( System.out );

        writeToFile( f );

        measuresMap = new HashMap<String, QueryMeasure>();
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

            final QueryMeasure queryMeasure = measuresMap.get( query );
            queryMeasure.printStats( out );

        }

        out.println( "-------------------" );
        out.println( "Grand total: " );
        total.printStats( out );
    }

    private class QueryMeasure
    {
        List<Long> measureOld = new ArrayList<Long>();

        List<Long> measureNew = new ArrayList<Long>();

        protected void addMeasure( boolean old, Long timeUsed )
        {
            if ( old )
            {
                measureOld.add( timeUsed );
            }
            else
            {
                measureNew.add( timeUsed );
            }
        }

        public void printStats( PrintStream out )
        {

            final ListInfo oldList = getListInfo( measureOld );
            final ListInfo newList = getListInfo( measureNew );

            final String format = "%-10s %-10s %-10s\n";
            out.printf( format, "Stat:", "DB:", "ElasticSearch:" );
            out.printf( format, "--", "--", "--" );
            out.printf( format, "Total#", oldList.totalHits, newList.totalHits );
            out.printf( format, "Min", oldList.minTime, newList.minTime );
            out.printf( format, "Max", oldList.maxTime, newList.maxTime );
            out.printf( format, "Avg", oldList.avgTime, newList.avgTime );
        }

        private ListInfo getListInfo( final List<Long> measures )
        {
            ListInfo listInfo = new ListInfo();

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
                listInfo.totalHits = measures.size();
                listInfo.avgTime = sum / measures.size();
                listInfo.maxTime = maxTime;
                listInfo.minTime = minTime;
            }
            else
            {
                listInfo.totalHits = -1;
                listInfo.avgTime = -1;
                listInfo.maxTime = -1;
                listInfo.minTime = -1;
            }

            return listInfo;
        }

        /*
        private String getInfoForList( final String measureName, final ListInfo listInfo )
        {
            StringBuffer buf = new StringBuffer();
            out.println( "Measures for : " + measureName + LS );

            if ( listInfo == null )
            {
                out.println( "No measures registred" + LS );
                return buf.toString();
            }

            out.println( " - Total hits: " + listInfo.totalHits + LS );
            out.println( " - Avg time: " + listInfo.avgTime + LS );
            out.println( " - Max time: " + listInfo.maxTime );
            out.println( " - Min time: " + listInfo.minTime );

            return buf.toString();
        }
        */
    }


    private class ListInfo
    {

        int totalHits = 0;

        long avgTime = -1;

        long maxTime = -1;

        long minTime = -1;

    }

}
