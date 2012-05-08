/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;
import java.util.Map;

import com.enonic.cms.store.dao.ContentDao;

public final class ContentEntityFetcherImpl
    implements ContentEntityFetcher
{

    private final ContentDao contentDao;


    public ContentEntityFetcherImpl( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public Map<ContentKey, ContentEntity> fetch( List<ContentKey> keys )
    {
        return contentDao.findByKeys( keys );
    }
}
