/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.services;

import com.enonic.cms.web.portal.PortalWebContext;

public interface ServicesProcessor
{
    public String getHandlerName();

    public void handle( final PortalWebContext context )
        throws Exception;
}
