/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentEagerFetches;
import com.enonic.cms.store.dao.FindContentByKeysCommand;

public final class ContentEntityFetcherImpl
    implements ContentEntityFetcher
{

    private final ContentDao contentDao;


    public ContentEntityFetcherImpl( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public ContentMap fetch( List<ContentKey> keys )
    {
        final FindContentByKeysCommand command =
            new FindContentByKeysCommand().contentKeys( new ArrayList<ContentKey>( keys ) ).eagerFetches(
                ContentEagerFetches.PRESET_FOR_PORTAL );
        return contentDao.findByKeys( command );
    }
}
