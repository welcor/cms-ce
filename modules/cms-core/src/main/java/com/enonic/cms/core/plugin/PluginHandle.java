/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin;

import org.joda.time.DateTime;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.core.plugin.context.PluginContext;

public interface PluginHandle
{
    public long getKey();

    public String getId();

    public String getName();

    public String getVersion();

    public boolean isActive();

    public DateTime getTimestamp();

    public ExtensionSet getExtensions();

    public PluginContext getContext();

    public PluginConfig getConfig();

    public void update();
}
