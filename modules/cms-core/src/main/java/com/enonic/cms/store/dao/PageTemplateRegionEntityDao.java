/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;

@Repository("pageTemplateRegionDao")
public final class PageTemplateRegionEntityDao
    extends AbstractBaseEntityDao<PageTemplateRegionEntity>
    implements PageTemplateRegionDao
{
    public PageTemplateRegionEntity findByKey( int key )
    {
        return get( PageTemplateRegionEntity.class, key );
    }
}
