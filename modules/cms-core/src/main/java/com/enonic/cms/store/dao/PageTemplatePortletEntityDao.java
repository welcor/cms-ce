/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletKey;

@Repository("pageTemplatePortletDao")
public final class PageTemplatePortletEntityDao
    extends AbstractBaseEntityDao<PageTemplatePortletEntity>
    implements PageTemplatePortletDao
{
    public PageTemplatePortletEntity findByKey( PageTemplatePortletKey key )
    {
        return get( PageTemplatePortletEntity.class, key );
    }
}
