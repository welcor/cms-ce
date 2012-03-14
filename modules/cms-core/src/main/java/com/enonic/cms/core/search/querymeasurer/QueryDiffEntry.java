package com.enonic.cms.core.search.querymeasurer;


import java.io.Serializable;

import com.google.common.collect.ImmutableSet;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

public class QueryDiffEntry
    implements Serializable
{
    private IndexQuerySignature querySignature;

    private ImmutableSet<ContentKey> inNewOnly;

    private ImmutableSet<ContentKey> inOldOnly;

    private ContentIndexQuery fullQuery;

    private String elasticSearchQuery;

    private String hibernateQuery;

    private int newSize;

    private int oldSize;

    private int newTotalCount;

    private int oldTotalCount;

    public QueryDiffEntry( final IndexQuerySignature querySignature, final ImmutableSet<ContentKey> inNewOnly,
                           final ImmutableSet<ContentKey> inOldOnly, final ContentIndexQuery fullQuery, ContentResultSet oldResultSet,
                           ContentResultSet newResultSet )
    {
        this.querySignature = querySignature;
        this.inNewOnly = inNewOnly;
        this.inOldOnly = inOldOnly;
        this.fullQuery = fullQuery;
        this.newSize = newResultSet.getKeys().size();
        this.oldSize = oldResultSet.getKeys().size();
        this.newTotalCount = newResultSet.getTotalCount();
        this.oldTotalCount = oldResultSet.getTotalCount();
    }

    public IndexQuerySignature getQuerySignature()
    {
        return querySignature;
    }

    public ImmutableSet<ContentKey> getInNewOnly()
    {
        return inNewOnly;
    }

    public void setInNewOnly( final ImmutableSet<ContentKey> inNewOnly )
    {
        this.inNewOnly = inNewOnly;
    }

    public ImmutableSet<ContentKey> getInOldOnly()
    {
        return inOldOnly;
    }

    public void setInOldOnly( final ImmutableSet<ContentKey> inOldOnly )
    {
        this.inOldOnly = inOldOnly;
    }

    public ContentIndexQuery getFullQuery()
    {
        return fullQuery;
    }

    public void setFullQuery( final ContentIndexQuery fullQuery )
    {
        this.fullQuery = fullQuery;
    }

    public int getNewSize()
    {
        return newSize;
    }

    public int getOldSize()
    {
        return oldSize;
    }

    public String getElasticSearchQuery()
    {
        return elasticSearchQuery;
    }

    public void setElasticSearchQuery( final String elasticSearchQuery )
    {
        this.elasticSearchQuery = elasticSearchQuery;
    }

    public String getHibernateQuery()
    {
        return hibernateQuery;
    }

    public void setHibernateQuery( final String hibernateQuery )
    {
        this.hibernateQuery = hibernateQuery;
    }

    public int getNewTotalCount()
    {
        return newTotalCount;
    }

    public int getOldTotalCount()
    {
        return oldTotalCount;
    }
}

