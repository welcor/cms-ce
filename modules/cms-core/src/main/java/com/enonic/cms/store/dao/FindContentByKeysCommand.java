package com.enonic.cms.store.dao;


import java.util.List;

import com.enonic.cms.core.content.ContentKey;

public class FindContentByKeysCommand
{
    private List<ContentKey> contentKeys;

    private ContentEagerFetches contentEagerFetches;

    private boolean byPassCache = false;

    public FindContentByKeysCommand contentKeys( List<ContentKey> value )
    {
        this.contentKeys = value;
        return this;
    }

    public FindContentByKeysCommand eagerFetches( ContentEagerFetches value )
    {
        this.contentEagerFetches = value;
        return this;
    }

    public FindContentByKeysCommand byPassCache( boolean value )
    {
        this.byPassCache = value;
        return this;
    }

    public List<ContentKey> getContentKeys()
    {
        return contentKeys;
    }

    public ContentEagerFetches getContentEagerFetches()
    {
        return contentEagerFetches;
    }

    public boolean isByPassCache()
    {
        return byPassCache;
    }
}
