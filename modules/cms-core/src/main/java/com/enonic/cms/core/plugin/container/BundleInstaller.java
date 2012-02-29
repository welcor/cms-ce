package com.enonic.cms.core.plugin.container;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.cms.api.util.LogFacade;

final class BundleInstaller
{
    private final static LogFacade LOG = LogFacade.get( BundleInstaller.class );

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
            LOG.errorCause( "Error updating plugin from location [{0}]", e, bundle.getLocation() );
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
            LOG.errorCause( "Error installing plugin from location [{0}]", e, location );
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
            LOG.errorCause( "Error occurred removing plugin [{0}]", e, bundle.getSymbolicName() );
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
