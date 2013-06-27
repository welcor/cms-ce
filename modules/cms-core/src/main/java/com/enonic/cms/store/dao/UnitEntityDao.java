/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.content.category.UnitEntity;
import com.enonic.cms.core.content.category.UnitKey;
import com.enonic.cms.store.support.EntityPageList;

@Repository("unitDao")
public final class UnitEntityDao
    extends AbstractBaseEntityDao<UnitEntity>
    implements UnitDao
{
    public UnitEntity findByKey( UnitKey key )
    {
        UnitEntity unit = get( UnitEntity.class, key );

        if ( unit == null )
        {
            return null;
        }

        return unit;
    }

    public List<UnitEntity> getAll()
    {
        return findByNamedQuery( UnitEntity.class, "UnitEntity.getAll" );
    }

    public EntityPageList<UnitEntity> findAll( int index, int count )
    {
        return findPageList( UnitEntity.class, "x.deleted = 0", index, count );
    }
}