/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;

public interface PageTemplateRegionDao
    extends EntityDao<PageTemplateRegionEntity>
{
    PageTemplateRegionEntity findByKey( int key );
}
