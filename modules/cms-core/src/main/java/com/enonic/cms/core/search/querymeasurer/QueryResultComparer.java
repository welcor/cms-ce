package com.enonic.cms.core.search.querymeasurer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private List<QueryDiffEntry> queryDiffEntries = new ArrayList<QueryDiffEntry>();

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

        QueryDiffEntry queryDiffEntry = new QueryDiffEntry( querySignature, newResultContentKeys, oldResultContentKeys, diff );
        queryDiffEntries.add( queryDiffEntry );
    }


    public List<QueryDiffEntry> getQueryDiffEntries()
    {
        return queryDiffEntries;
    }
}
