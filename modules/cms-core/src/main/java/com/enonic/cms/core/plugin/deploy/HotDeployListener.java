package com.enonic.cms.core.plugin.deploy;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import com.enonic.cms.core.plugin.PluginManager;

final class HotDeployListener
    extends FileAlterationListenerAdaptor
{
    private final PluginManager installer;

    public HotDeployListener( final PluginManager installer )
    {
        this.installer = installer;
    }

    @Override
    public void onFileCreate( final File file )
    {
        this.installer.install( file );
    }

    @Override
    public void onFileChange( final File file )
    {
        this.installer.install( file );
    }

    @Override
    public void onFileDelete( final File file )
    {
        this.installer.uninstall( file );
    }
}
