/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.hook;

import java.io.IOException;
import java.util.jar.Manifest;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;
import org.eclipse.osgi.baseadaptor.hooks.BundleFileWrapperFactoryHook;

final class TransformerHook
    implements BundleFileWrapperFactoryHook
{
    private final ManifestTransformer manifestTransformer;

    public TransformerHook()
    {
        this.manifestTransformer = new ManifestTransformer();
    }

    public BundleFile wrapBundleFile( final BundleFile file, final Object content, final BaseData data, final boolean base )
        throws IOException
    {
        if (!base) {
            return null;
        }

        final Manifest mf = this.manifestTransformer.transform( file );
        final ManifestFileEntry entry = new ManifestFileEntry( mf );
        return new ManifestWrappedFile( file, entry );
    }
}
