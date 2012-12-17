/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.services;

import com.enonic.vertical.VerticalLogger;

public final class VerticalUserServicesLogger
    extends VerticalLogger
{
    public static void warnUserServices( String message, Throwable throwable )
    {
        error( message, throwable );
    }

    public static void errorUserServices( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalUserServicesException( message, throwable );
    }
}
