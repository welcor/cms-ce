package com.enonic.cms.api.plugin;

import java.util.Map;

import com.enonic.cms.api.plugin.ext.Extension;

public interface PluginContext
{
    public String getId();

    public String getName();

    public String getVersion();

    public PluginConfig getConfig();
    
    public Map<String, Object> getServices();

    public void register(Extension extension);
}
