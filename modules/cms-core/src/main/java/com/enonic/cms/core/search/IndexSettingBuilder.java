package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.Settings;

public interface IndexSettingBuilder
{
    public Settings buildIndexSettings();
}
