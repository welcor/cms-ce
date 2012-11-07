/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

public final class LocaleParsingException
    extends RuntimeException
{
    public LocaleParsingException( final String message, final Throwable t )
    {
        super( message, t );
    }

    public LocaleParsingException( final String message )
    {
        super( message );
    }
}
