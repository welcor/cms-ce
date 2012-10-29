package com.enonic.cms.core.portal.datasource.handler.legacy;

import javax.annotation.Nonnull;

public final class GetRandomContentParams
{
    public int count = 10;

    @Nonnull
    public int[] categoryKeys;

    public boolean recursive = false;

    public int childrenLevel = 1;

    public int minPriority = 0;

    public int parentLevel = 0;

    public int parentChildrenLevel = 0;
}
