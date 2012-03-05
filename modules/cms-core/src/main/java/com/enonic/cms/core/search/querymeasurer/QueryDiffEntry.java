package com.enonic.cms.core.search.querymeasurer;


import java.io.Serializable;
import java.util.HashSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;

public class QueryDiffEntry
    implements Serializable
{
    private IndexQuerySignature querySignature;

    private HashSet<ContentKey> newResultContentKeys;

    private HashSet<ContentKey> oldResultContentKeys;

    private ImmutableSet<ContentKey> diff;

    public QueryDiffEntry( IndexQuerySignature querySignature, HashSet<ContentKey> newResultContentKeys,
                           HashSet<ContentKey> oldResultContentKeys, Sets.SetView<ContentKey> diff )
    {
        this.querySignature = querySignature;
        this.newResultContentKeys = newResultContentKeys;
        this.oldResultContentKeys = oldResultContentKeys;
        this.diff = diff.immutableCopy();
    }


    public IndexQuerySignature getQuerySignature()
    {
        return querySignature;
    }

    public HashSet<ContentKey> getNewResultContentKeys()
    {
        return newResultContentKeys;
    }

    public HashSet<ContentKey> getOldResultContentKeys()
    {
        return oldResultContentKeys;
    }


    public ImmutableSet<ContentKey> getDiff()
    {
        return diff;
    }


}

