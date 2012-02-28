package com.enonic.cms.core.plugin.deploy;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.plugin.PluginManager;

public class HotDeployListenerTest
{
    private PluginManager pluginManager;
    private HotDeployListener listener;
    private File pluginFile;

    @Before
    public void setUp()
        throws Exception
    {
        this.pluginManager = Mockito.mock(PluginManager.class);
        this.listener = new HotDeployListener(this.pluginManager);
        this.pluginFile = new File("plugin.jar");
    }

    @Test
    public void testCreate()
    {
        this.listener.onFileCreate(this.pluginFile);
        Mockito.verify(this.pluginManager, Mockito.times(1)).install(this.pluginFile);
    }

    @Test
    public void testChange()
    {
        this.listener.onFileChange(this.pluginFile);
        Mockito.verify(this.pluginManager, Mockito.times(1)).install(this.pluginFile);
    }

    @Test
    public void testDelete()
    {
        this.listener.onFileDelete(this.pluginFile);
        Mockito.verify(this.pluginManager, Mockito.times(1)).uninstall(this.pluginFile);
    }
}
