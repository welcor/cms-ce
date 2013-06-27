/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.deploy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.cms.core.plugin.PluginManager;

public class HotDeployTaskTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStartup()
        throws Exception
    {
        final PluginManager pluginManager = Mockito.mock( PluginManager.class );

        final HotDeployTask task = new HotDeployTask();
        task.setScanPeriod( 100L );
        task.setDeployDir( this.folder.newFolder( "plugins" ) );
        task.setPluginManager( pluginManager );

        task.start();
        task.stop();
    }
}
