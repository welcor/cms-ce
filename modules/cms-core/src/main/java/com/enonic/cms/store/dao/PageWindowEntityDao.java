/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Repository;

import com.enonic.cms.core.structure.page.PageWindowEntity;

@Repository("pageWindowDao")
public final class PageWindowEntityDao
    extends AbstractBaseEntityDao<PageWindowEntity>
    implements PageWindowDao
{
    public PageWindowEntity findByKey( int key )
    {
        return get( PageWindowEntity.class, key );
    }

    public int deleteByPageKeyAndTemplateRegionKey( Integer[] pageKeys, int[] regionKeys )
    {
        Integer[] keys = ArrayUtils.toObject( regionKeys );

        return deleteByNamedQuery( "PageWindowEntity.deleteByPageKeyAndTemplateRegionKey",
                                   new String[]{"pageKeys", "regionKeys"},
                                   new Object[][]{pageKeys, keys} );
    }

    public int deleteByPageKeys( Integer[] pageKeys )
    {
        return deleteByNamedQuery( "PageWindowEntity.deleteByPageKeys", new String[]{"pageKeys"}, new Object[][]{pageKeys} );
    }
}
