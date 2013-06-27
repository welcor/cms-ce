/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import javax.annotation.Nonnull;

public final class GetCategoriesParams
{
    @Nonnull
    public Integer categoryKey;

    public int levels = 0;

    public boolean includeContentCount = false;

    public boolean includeTopCategory = true;
}
