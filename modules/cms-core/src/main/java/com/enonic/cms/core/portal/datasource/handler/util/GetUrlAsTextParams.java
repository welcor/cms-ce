/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import javax.annotation.Nonnull;

public final class GetUrlAsTextParams
{
    @Nonnull
    public String url;

    public int timeout = 5000;

    public String encoding = "UTF-8";
}
