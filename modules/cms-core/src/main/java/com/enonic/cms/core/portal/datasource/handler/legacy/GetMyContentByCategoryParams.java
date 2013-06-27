/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.legacy;

import javax.annotation.Nonnull;

public final class GetMyContentByCategoryParams
{
    public String query = "";

    @Nonnull
    public int[] categoryKeys;

    public boolean recursive = false;

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

    public int[] contentTypeKeys;
}
