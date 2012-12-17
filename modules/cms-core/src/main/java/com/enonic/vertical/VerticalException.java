/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import com.enonic.cms.core.util.LoggingUtil;

/**
 * Root exception for all Vertical exceptions.
 */
public class VerticalException
    extends RuntimeException
{
    public VerticalException( String message )
    {
        super( message );
    }

    public VerticalException( String message, Throwable cause )
    {
        super( LoggingUtil.formatCause( message, cause ), cause );
    }
}
