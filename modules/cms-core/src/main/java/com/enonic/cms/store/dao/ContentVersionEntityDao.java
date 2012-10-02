/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;

@Repository("contentVersionDao")
public final class ContentVersionEntityDao
    extends AbstractBaseEntityDao<ContentVersionEntity>
    implements ContentVersionDao
{

    public ContentVersionEntity findByKey( ContentVersionKey key )
    {
        return get( ContentVersionEntity.class, key );
    }

}