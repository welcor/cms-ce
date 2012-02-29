package com.enonic.cms.core.plugin.hook;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Properties;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.baseadaptor.hooks.AdaptorHook;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

final class LoggingHook
    implements AdaptorHook
{
    public void initialize( final BaseAdaptor adaptor )
    {
    }

    public void frameworkStart( final BundleContext context )
        throws BundleException
    {
        context.addBundleListener( new BundleEventLogger() );
    }

    public void frameworkStop( final BundleContext context )
        throws BundleException
    {

    }

    public void frameworkStopping( final BundleContext context )
    {

    }

    public void addProperties( final Properties properties )
    {

    }

    public URLConnection mapLocationToURLConnection( final String location )
        throws IOException
    {
        return null;
    }

    public void handleRuntimeError( final Throwable error )
    {
    }

    public FrameworkLog createFrameworkLog()
    {
        return new FrameworkLogImpl();
    }
}
