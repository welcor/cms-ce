/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.client;


import org.springframework.stereotype.Component;

@Component(value = "remoteClient")
public final class InternalRemoteClient
    extends InternalClientImpl
{
    InternalRemoteClient()
    {
        super( true );
    }
}
