/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin;

import java.io.File;
import java.util.List;

import com.enonic.cms.api.plugin.ext.Extension;

public interface PluginManager
{
    public List<PluginHandle> getPlugins();

    public ExtensionSet getExtensions();

    public PluginHandle findPluginByKey( long key );

    public void install( File file );

    public void uninstall( File file );

    /**
     * This is a hack. It will register a local extension and is used for ldap extension registration. If we
     * fix up our integration tests, this method can be removed.
     */
    public void registerLocalExtension( Extension ext );
}
