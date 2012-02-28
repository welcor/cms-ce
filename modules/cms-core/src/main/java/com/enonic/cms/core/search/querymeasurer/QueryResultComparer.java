package com.enonic.cms.core.search.querymeasurer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/20/12
 * Time: 8:57 AM
 */
public class QueryResultComparer
{
    private List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();

    private List<IndexQuerySignature> checkedQueries = new ArrayList<IndexQuerySignature>();

    private final static String LS = System.getProperty( "line.separator" );

    public void compareResults( ContentIndexQuery query, ContentResultSet resultNew, ContentResultSet resultOld )
    {
        final IndexQuerySignature querySignature = QuerySignatureResolver.createQuerySignature( query );

        if ( checkedQueries.contains( querySignature ) )
        {
            return;
        }

        final HashSet<ContentKey> newResultContentKeys = Sets.newHashSet( resultNew.getKeys() );
        final HashSet<ContentKey> oldResultContentKeys = Sets.newHashSet( resultOld.getKeys() );
        Sets.SetView<ContentKey> diff = Sets.symmetricDifference( newResultContentKeys, oldResultContentKeys );

        checkedQueries.add( querySignature );

        if ( diff.isEmpty() )
        {
            return;
        }

        DiffEntry diffEntry = new DiffEntry( querySignature, newResultContentKeys, oldResultContentKeys );
        diffEntries.add( diffEntry );
    }


    public synchronized void flush()
    {
        File f = new File( "querydiff-log" + Calendar.getInstance().getTime() + ".log" );

        final String queryDiffData = this.toString();

        System.out.println( queryDiffData );

        try
        {
            FileUtils.writeStringToFile( f, queryDiffData, "UTF-8" );
        }
        catch ( IOException e )
        {
            System.out.println( "Failed to write queryDiffData to disk " );
        }
    }


    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        buf.append( LS + LS + LS );
        buf.append( "-----------------------" );
        buf.append( "Diff results: " + LS + LS );

        for ( DiffEntry entry : diffEntries )
        {
            buf.append( entry.toString() );
        }

        return buf.toString();

    }

    private class DiffEntry
    {

        private IndexQuerySignature querySignature;

        private HashSet<ContentKey> newResultContentKeys;

        private HashSet<ContentKey> oldResultContentKeys;

        private DiffEntry( IndexQuerySignature querySignature, HashSet<ContentKey> newResultContentKeys,
                           HashSet<ContentKey> oldResultContentKeys )
        {
            this.querySignature = querySignature;
            this.newResultContentKeys = newResultContentKeys;
            this.oldResultContentKeys = oldResultContentKeys;
        }

        @Override
        public String toString()
        {
            StringBuffer buf = new StringBuffer();

            Sets.SetView<ContentKey> onlyInNew = Sets.difference( newResultContentKeys, oldResultContentKeys );
            Sets.SetView<ContentKey> onlyInOld = Sets.difference( oldResultContentKeys, newResultContentKeys );

            buf.append( "****" );
            buf.append( "Query: " + querySignature.getQueryDisplayValue() + LS + LS );

            buf.append( "Old total : " + oldResultContentKeys.size() + LS );
            buf.append( "New total : " + newResultContentKeys.size() + LS );
            buf.append( LS );

            if ( !onlyInNew.isEmpty() )
            {
                buf.append( "* Only in new: " + LS );

                for ( ContentKey key : onlyInNew )
                {
                    buf.append( key.toString() + LS );
                }
            }

            if ( !onlyInOld.isEmpty() )
            {
                buf.append( "* Only in old: " + LS );

                for ( ContentKey key : onlyInNew )
                {
                    buf.append( key.toString() + LS );
                }
            }

            buf.append( "-----------------" + LS );

            return buf.toString();
        }
    }


}
