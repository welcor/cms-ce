package com.enonic.cms.core.portal.datasource.handler.legacy;

import javax.annotation.Nonnull;

public final class FindContentByCategoryParams
{
    public String search = "";

    public String operator = "AND";

    @Nonnull
    public int[] categories;

    public boolean includeSubCategories = false;

    public String orderBy = "";

    public int index = 0;

    public int count = 10;

    public boolean titlesOnly = false;

    public int childrenLevel = 1;

    public int parentLevel = 0;

    public int parentChildrenLevel = 0;

    public boolean relatedTitlesOnly = false;

    public boolean includeTotalCount = false;

    public boolean includeUserRights = false;

    public int[] contentTypes;
}
