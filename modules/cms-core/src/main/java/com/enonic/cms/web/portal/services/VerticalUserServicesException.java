/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.services;

import com.enonic.vertical.VerticalException;

public class VerticalUserServicesException
    extends VerticalException
{
    public VerticalUserServicesException( String message )
    {
        super( message );
    }

    public VerticalUserServicesException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
