/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

public final class LocalizationResourceException
    extends RuntimeException
{
    public LocalizationResourceException( final String message, final Throwable t )
    {
        super( message, t );
    }

    public LocalizationResourceException( final String message )
    {
        super( message );
    }
}
