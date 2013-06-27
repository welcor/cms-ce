/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.hook;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;

final class ManifestWrappedFile
    extends BundleFile
{
    private final BundleFile wrapped;

    private final ManifestFileEntry mfEntry;

    public ManifestWrappedFile( final BundleFile wrapped, final ManifestFileEntry mfEntry )
    {
        this.wrapped = wrapped;
        this.mfEntry = mfEntry;
    }

    @Override
    public File getBaseFile()
    {
        return this.wrapped.getBaseFile();
    }

    @Override
    public File getFile( final String path, final boolean nativeCode )
    {
        return this.wrapped.getFile( path, nativeCode );
    }

    @Override
    public BundleEntry getEntry( final String path )
    {
        if ( path.equals( this.mfEntry.getName() ) )
        {
            return this.mfEntry;
        }

        return this.wrapped.getEntry( path );
    }

    @Override
    public Enumeration getEntryPaths( final String path )
    {
        return this.wrapped.getEntryPaths( path );
    }

    @Override
    public void close()
        throws IOException
    {
        this.wrapped.close();
    }

    @Override
    public void open()
        throws IOException
    {
        this.wrapped.open();
    }

    @Override
    public boolean containsDir( final String dir )
    {
        return this.wrapped.containsDir( dir );
    }
}
