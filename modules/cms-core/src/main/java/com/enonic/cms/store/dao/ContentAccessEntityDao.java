/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.security.group.GroupKey;

@Repository("contentAccessDao")
public final class ContentAccessEntityDao
    extends AbstractBaseEntityDao<ContentAccessEntity>
    implements ContentAccessDao
{
    public void deleteByGroupKey( GroupKey groupKey )
    {
        deleteByNamedQuery( "ContentAccessEntity.deleteByGroupKey", "groupKey", groupKey );
    }
}
