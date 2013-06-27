/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import javax.annotation.Nonnull;

public final class GetContentParams
{
    @Nonnull
    public int[] contentKeys;

    public String query = "";

    public String orderBy = "";

    public int index = 0;

    public int count = 10;

    public boolean includeData = true;

    public int childrenLevel = 1;

    public int parentLevel = 0;

    public String facets;
}
