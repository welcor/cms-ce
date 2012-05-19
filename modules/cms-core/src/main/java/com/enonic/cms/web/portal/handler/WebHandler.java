package com.enonic.cms.web.portal.handler;

import com.enonic.cms.core.SitePath;

public interface WebHandler
{
    public boolean canHandle( SitePath sitePath );

    public void handle( WebContext context )
        throws Exception;
}
