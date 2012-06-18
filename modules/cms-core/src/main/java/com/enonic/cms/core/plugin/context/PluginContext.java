package com.enonic.cms.core.plugin.context;

import java.util.Map;

import com.enonic.cms.api.plugin.PluginConfig;
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
