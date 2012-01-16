package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.ImmutableSettings;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/16/12
 * Time: 10:04 AM
 */
public interface IndexSettingsBuilder
{
    public ImmutableSettings.Builder buildSettings();
}
