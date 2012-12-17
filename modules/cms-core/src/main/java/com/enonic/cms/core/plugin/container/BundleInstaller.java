package com.enonic.cms.core.plugin.container;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BundleInstaller
{
    private final static Logger LOG = LoggerFactory.getLogger( BundleInstaller.class );

    private final BundleContext context;

    public BundleInstaller( final BundleContext context )
    {
        this.context = context;
    }

    public void install( final File file )
    {
        final String url = toLocation( file );
        final Bundle bundle = findBundle( url );

        if ( bundle != null )
        {
            doUpdate( bundle );
        }
        else
        {
            doInstall( url );
        }
    }

    public void uninstall( final File file )
    {
        final String url = toLocation( file );
        final Bundle bundle = findBundle( url );
        if ( bundle != null )
        {
            doUninstall( bundle );
        }
    }

    private String toLocation( final File file )
    {
        try
        {
            return file.toURI().toURL().toExternalForm();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private void doUpdate( final Bundle bundle )
    {
        try
        {
            bundle.update();
            bundle.start( 0 );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error updating plugin from location [{}]", bundle.getLocation(), e );
        }
    }

    private void doInstall( final String location )
    {
        try
        {
            final Bundle bundle = this.context.installBundle( location );
            bundle.start( 0 );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error installing plugin from location [{}]", location, e );
        }
    }

    private void doUninstall( final Bundle bundle )
    {
        try
        {
            bundle.uninstall();
        }
        catch ( final Exception e )
        {
            LOG.error( "Error occurred removing plugin [{}]", bundle.getSymbolicName(), e );
        }
    }

    private Bundle findBundle( final String location )
    {
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if ( location.equals( bundle.getLocation() ) )
            {
                return bundle;
            }
        }

        return null;
    }
}
