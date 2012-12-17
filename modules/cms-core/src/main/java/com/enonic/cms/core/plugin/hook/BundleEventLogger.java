package com.enonic.cms.core.plugin.hook;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.plugin.util.OsgiHelper;

final class BundleEventLogger
    implements BundleListener
{
    private final static Logger LOG = LoggerFactory.getLogger( BundleEventLogger.class );

    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();
        if ( OsgiHelper.isFrameworkBundle( bundle ) )
        {
            return;
        }

        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
                LOG.info( "Installed plugin [{}] from [{}]", bundle.getSymbolicName(), bundle.getLocation() );
                break;
            case BundleEvent.UNINSTALLED:
                LOG.info( "Uninstalled plugin [{}] from [{}]", bundle.getSymbolicName(), bundle.getLocation() );
                break;
            case BundleEvent.STARTED:
                LOG.info( "Started plugin [{}]", bundle.getSymbolicName() );
                break;
        }
    }
}
