package com.enonic.cms.core.portal.datasource.handler.content;

import javax.annotation.Nonnull;

public final class GetContentVersionParams
{
    @Nonnull
    public int[] versionKeys;

    public int childrenLevel = 1;
}
