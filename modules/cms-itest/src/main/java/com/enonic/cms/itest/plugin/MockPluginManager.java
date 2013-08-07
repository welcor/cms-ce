/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.plugin;

import java.io.File;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.PluginManager;

@Component
@Profile("itest")
public class MockPluginManager
    implements PluginManager
{
    public List<PluginHandle> getPlugins()
    {
        return Lists.newArrayList();
    }

    public PluginHandle findPluginByKey( long key )
    {
        return null;
    }

    public void install( final File file )
    {
    }

    public void uninstall( final File file )
    {
    }
}
