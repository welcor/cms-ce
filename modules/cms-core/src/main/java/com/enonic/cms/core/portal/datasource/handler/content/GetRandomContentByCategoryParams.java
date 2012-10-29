package com.enonic.cms.core.portal.datasource.handler.content;

import javax.annotation.Nonnull;

public final class GetRandomContentByCategoryParams
{
    @Nonnull
    public int[] categoryKeys;

    public int levels = 1;

    public String query = "";

    public int count = 10;

    public boolean includeData = true;

    public int childrenLevel = 1;

    public int parentLevel = 0;
}
