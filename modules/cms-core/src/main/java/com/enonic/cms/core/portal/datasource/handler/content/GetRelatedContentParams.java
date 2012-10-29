package com.enonic.cms.core.portal.datasource.handler.content;

import javax.annotation.Nonnull;

public final class GetRelatedContentParams
{
    @Nonnull
    public int[] contentKeys;

    public int relation = 1;

    public String query = "";

    public String orderBy = "";

    public int index = 0;

    public int count = 10;

    public boolean includeData = true;

    public int childrenLevel = 1;

    public int parentLevel = 0;

    public boolean requireAll = false;
}
