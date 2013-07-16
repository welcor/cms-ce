/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletKey;

public interface PageTemplatePortletDao
    extends EntityDao<PageTemplatePortletEntity>
{
    PageTemplatePortletEntity findByKey( PageTemplatePortletKey key );
}
