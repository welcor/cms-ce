package com.enonic.cms.core.plugin.manager;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

final class LocalServiceReference
    implements ServiceReference
{
    public final static LocalServiceReference INSTANCE = new LocalServiceReference();

    private LocalServiceReference()
    {
        // Do nothing
    }

    @Override
    public Object getProperty( final String s )
    {
        return null;
    }

    @Override
    public String[] getPropertyKeys()
    {
        return new String[0];
    }

    @Override
    public Bundle getBundle()
    {
        return null;
    }

    @Override
    public Bundle[] getUsingBundles()
    {
        return new Bundle[0];
    }

    @Override
    public boolean isAssignableTo( final Bundle bundle, final String s )
    {
        return false;
    }

    @Override
    public int compareTo( final Object o )
    {
        return 0;
    }
}
