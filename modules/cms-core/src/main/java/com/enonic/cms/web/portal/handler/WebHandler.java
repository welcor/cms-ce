package com.enonic.cms.web.portal.handler;

import com.enonic.cms.web.portal.PortalWebContext;

public interface WebHandler
{
    public boolean canHandle( PortalWebContext context );

    public void handle( PortalWebContext context )
        throws Exception;
}
