/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.structure.page.PageWindowEntity;

public interface PageWindowDao
    extends EntityDao<PageWindowEntity>
{
    PageWindowEntity findByKey( int key );

    int deleteByPageKeyAndTemplateRegionKey( Integer[] pageKeys, int[] regionKeys );

    int deleteByPageKeys( Integer[] pageKeys );
}
