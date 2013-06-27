/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.menu;

import javax.annotation.Nonnull;

public final class GetMenuBranchParams
{
    @Nonnull
    public Integer menuItemKey;

    public boolean includeTopLevel = false;

    public int startLevel = 0;

    public int levels = 0;
}
