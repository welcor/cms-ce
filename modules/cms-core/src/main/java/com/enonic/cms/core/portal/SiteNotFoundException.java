/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import com.enonic.cms.core.NotFoundErrorType;
import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.structure.SiteKey;

public class SiteNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{
    private SiteKey siteKey;

    private String message;


    public SiteNotFoundException( SiteKey siteKey )
    {
        this.siteKey = siteKey;
        message = "Site not found: '" + siteKey + "'";
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getMessage()
    {
        return message;
    }
}
