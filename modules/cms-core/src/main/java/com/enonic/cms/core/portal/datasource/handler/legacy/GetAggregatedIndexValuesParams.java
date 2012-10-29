package com.enonic.cms.core.portal.datasource.handler.legacy;

import javax.annotation.Nonnull;

public final class GetAggregatedIndexValuesParams
{
    @Nonnull
    public String field;

    public int[] categoryKeys;

    public boolean recursive = false;

    public int[] contentTypeKeys;
}
