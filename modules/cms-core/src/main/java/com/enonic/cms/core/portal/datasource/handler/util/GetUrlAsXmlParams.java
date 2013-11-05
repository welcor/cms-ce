/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import javax.annotation.Nonnull;

public final class GetUrlAsXmlParams
    implements GetUrlParams
{
    @Nonnull
    public String url;

    public int timeout = DEFAULT_TIMEOUT_FOR_CONNECT;

    public int readTimeout = DEFAULT_TIMEOUT_FOR_READ;
}
