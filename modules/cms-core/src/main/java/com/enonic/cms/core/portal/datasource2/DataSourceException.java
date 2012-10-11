package com.enonic.cms.core.portal.datasource2;

import java.text.MessageFormat;

public final class DataSourceException
    extends RuntimeException
{
    public DataSourceException( final String message, final Object... args )
    {
        super( MessageFormat.format( message, args ) );
    }

    public DataSourceException withCause( final Throwable cause )
    {
        if ( cause != null )
        {
            initCause( cause );
        }

        return this;
    }
}
