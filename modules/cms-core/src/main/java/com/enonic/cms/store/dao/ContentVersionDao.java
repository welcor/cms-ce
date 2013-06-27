/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;


public interface ContentVersionDao
    extends EntityDao<ContentVersionEntity>
{
    ContentVersionEntity findByKey( ContentVersionKey key );

}
