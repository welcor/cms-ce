package com.enonic.cms.core.search.querymeasurer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.TranslatedQuery;
import com.enonic.cms.core.content.index.translator.ContentQueryTranslator;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.QueryTranslator;

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

    private QueryTranslator elasticSearchTranslator;

    private ContentQueryTranslator hibernateTranslator = new ContentQueryTranslator();

    public void compareResults( ContentIndexQuery query, ContentResultSet resultNew, ContentResultSet resultOld )
    {
        final IndexQuerySignature querySignature = QuerySignatureResolver.createQuerySignature( query );

        if ( checkedQueries.contains( querySignature ) )
        {
            return;
        }

        checkedQueries.add( querySignature );

        if ( resultNew.getKeys().size() == resultOld.getKeys().size() && resultNew.getTotalCount() == resultOld.getTotalCount() )
        {
            return;
        }

        final HashSet<ContentKey> newResultContentKeys = Sets.newHashSet( resultNew.getKeys() );
        final HashSet<ContentKey> oldResultContentKeys = Sets.newHashSet( resultOld.getKeys() );

        ImmutableSet newOnly = Sets.difference( newResultContentKeys, oldResultContentKeys ).immutableCopy();
        ImmutableSet oldOnly = Sets.difference( oldResultContentKeys, newResultContentKeys ).immutableCopy();

        if ( newOnly.isEmpty() && oldOnly.isEmpty() )
        {
            return;
        }

        QueryDiffEntry queryDiffEntry = new QueryDiffEntry( querySignature, newOnly, oldOnly, query, resultOld, resultNew );
        queryDiffEntries.add( queryDiffEntry );

        try
        {
            final SearchSourceBuilder esQuery = elasticSearchTranslator.build( query );
            queryDiffEntry.setElasticSearchQuery( esQuery.toString() );

            final TranslatedQuery hibernateQuery = hibernateTranslator.translate( query );
            queryDiffEntry.setHibernateQuery( hibernateQuery.getQuery() );


        }
        catch ( Exception e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public List<QueryDiffEntry> getQueryDiffEntries()
    {
        return queryDiffEntries;
    }

    public void clear()
    {
        queryDiffEntries.clear();
    }

    @Autowired
    public void setElasticSearchTranslator( final QueryTranslator elasticSearchTranslator )
    {
        this.elasticSearchTranslator = elasticSearchTranslator;
    }

}
