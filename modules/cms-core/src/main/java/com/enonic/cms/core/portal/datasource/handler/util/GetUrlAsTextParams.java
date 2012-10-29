package com.enonic.cms.core.portal.datasource.handler.util;

import javax.annotation.Nonnull;

public final class GetUrlAsTextParams
{
    @Nonnull
    public String url;

    public int timeout = 5000;

    public String encoding = "ISO-8859-1";
}
