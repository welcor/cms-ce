package com.enonic.vertical.work;

/**
 * This class implements the exception that is used by work service.
 */
public final class WorkException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public WorkException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public WorkException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
