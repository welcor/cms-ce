package com.enonic.cms.core.plugin.hook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;

final class ManifestFileEntry
    extends BundleEntry
{
    private final byte[] bytes;

    public ManifestFileEntry( final Manifest mf )
        throws IOException
    {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mf.write( bos );
        this.bytes = bos.toByteArray();
    }

    @Override
    public URL getFileURL()
    {
        return null;
    }

    @Override
    public InputStream getInputStream()
    {
        return new ByteArrayInputStream( this.bytes );
    }

    @Override
    public URL getLocalURL()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return JarFile.MANIFEST_NAME;
    }

    @Override
    public long getSize()
    {
        return this.bytes.length;
    }

    @Override
    public long getTime()
    {
        return -1;
    }

    @Override
    public byte[] getBytes()
    {
        return this.bytes;
    }
}
