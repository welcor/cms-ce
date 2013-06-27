/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.interceptor;

import com.enonic.cms.web.portal.PortalWebContext;

public interface RequestInterceptor
{
    public boolean preHandle(PortalWebContext context)
        throws Exception;

    public void postHandle(PortalWebContext context)
        throws Exception;
}
