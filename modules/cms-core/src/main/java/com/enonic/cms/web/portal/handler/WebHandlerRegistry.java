/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.handler;

import com.enonic.cms.web.portal.PortalWebContext;

public interface WebHandlerRegistry
{
    public WebHandler find( final PortalWebContext context );
}
