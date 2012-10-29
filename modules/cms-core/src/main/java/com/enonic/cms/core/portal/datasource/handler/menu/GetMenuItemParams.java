package com.enonic.cms.core.portal.datasource.handler.menu;

import javax.annotation.Nonnull;

public final class GetMenuItemParams
{
    @Nonnull
    public Integer menuItemKey;

    public boolean withParents = false;
}
