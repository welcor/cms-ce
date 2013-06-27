/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import com.enonic.cms.core.NotFoundErrorType;
import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.structure.SitePath;

/**
 * Aug 28, 2009
 */
public class PageTemplateNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{

    public PageTemplateNotFoundException( SitePath sitePath )
    {
        super( "Could not find any page template for path '" + sitePath.getLocalPath().toString() + "' on site: " + sitePath.getSiteKey() );
    }
}
