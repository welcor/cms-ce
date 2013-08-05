/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

public class RemoteUserStorePluginException
    extends RuntimeException
{
    public RemoteUserStorePluginException( String message )
    {
        super( message );
    }

    public RemoteUserStorePluginException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public RemoteUserStorePluginException( Throwable cause )
    {
        super( cause );
    }
}
