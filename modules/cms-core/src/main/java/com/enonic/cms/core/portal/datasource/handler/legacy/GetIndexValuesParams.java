package com.enonic.cms.core.portal.datasource.handler.legacy;

import javax.annotation.Nonnull;

public final class GetIndexValuesParams
{
    @Nonnull
    public String field;

    public int[] categoryKeys;

    public boolean recursive = false;

    public int[] contentTypeKeys;

    public int index = 0;

    public int count = 200;

    public boolean distinct = true;

    public String order = "ASC";
}
