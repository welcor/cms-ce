/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.structure.page.PageEntity;

@Repository("pageDao")
public final class PageEntityDao
    extends AbstractBaseEntityDao<PageEntity>
    implements PageDao
{
    public PageEntity findByKey( int pageKey )
    {
        return get( PageEntity.class, pageKey );
    }

    public List<PageEntity> findByTemplateKeys( List<Integer> pageTemplateKeys )
    {
        return findByNamedQuery( PageEntity.class, "PageEntity.findByTemplateKeys", "keys", pageTemplateKeys );
    }
}
