package com.enonic.cms.core.search.querymeasurer;


import java.io.Serializable;
import java.util.HashSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryDiffEntry
    implements Serializable
{
    private IndexQuerySignature querySignature;

    private ImmutableSet<ContentKey> inNewOnly;

    private ImmutableSet<ContentKey> inOldOnly;

    private ContentIndexQuery fullQuery;

    private int newSize;

    private int oldSize;

    public QueryDiffEntry( final IndexQuerySignature querySignature, final ImmutableSet<ContentKey> inNewOnly,
                           final ImmutableSet<ContentKey> inOldOnly, final ContentIndexQuery fullQuery, final int newSize,
                           final int oldSize )
    {
        this.querySignature = querySignature;
        this.inNewOnly = inNewOnly;
        this.inOldOnly = inOldOnly;
        this.fullQuery = fullQuery;
        this.newSize = newSize;
        this.oldSize = oldSize;
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
}

