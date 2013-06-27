/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.client;


import org.springframework.stereotype.Component;

@Component(value = "localClient")
public final class InternalLocalClient
    extends InternalClientImpl
{
    public InternalLocalClient()
    {
        super( false );
    }
}
