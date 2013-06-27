/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.legacy;

import javax.annotation.Nonnull;

public final class GetSuperCategoryNamesParams
{
    @Nonnull
    public Integer categoryKey;

    public boolean includeContentCount = false;

    public boolean includeCurrent = false;
}
