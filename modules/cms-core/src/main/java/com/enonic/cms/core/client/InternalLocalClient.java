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
