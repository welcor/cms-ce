/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.content;

import javax.annotation.Nonnull;

public final class GetContentVersionParams
{
    @Nonnull
    public int[] versionKeys;

    public int childrenLevel = 1;
}
