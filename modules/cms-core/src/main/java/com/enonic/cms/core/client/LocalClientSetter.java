/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.client.ClientFactory;
import com.enonic.cms.api.client.LocalClient;

/**
 * This class registers the local client into the client factory.
 */
@Component
public final class LocalClientSetter
{
    /**
     * Set the local client.
     */
    @Autowired
    @Qualifier("localClient")
    public void setLocalClient( LocalClient client )
    {
        ClientFactory.setLocalClient( client );
    }
}
