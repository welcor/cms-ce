/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import com.enonic.cms.core.SiteKey;

public class SiteContext
{
    private SiteKey siteKey;

    private boolean authenticationLoggingEnabled;

    public SiteContext( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public boolean isAuthenticationLoggingEnabled()
    {
        return authenticationLoggingEnabled;
    }

    public void setAuthenticationLoggingEnabled( boolean value )
    {
        this.authenticationLoggingEnabled = value;
    }
}
