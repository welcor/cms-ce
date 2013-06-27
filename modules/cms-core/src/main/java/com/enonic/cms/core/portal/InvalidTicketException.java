/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

public class InvalidTicketException
    extends RuntimeException
    implements ForbiddenErrorType
{
    public InvalidTicketException()
    {
        super( "Invalid ticket" );
    }
}
