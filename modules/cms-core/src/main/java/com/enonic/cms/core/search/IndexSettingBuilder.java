/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.Settings;

public interface IndexSettingBuilder
{
    public Settings buildIndexSettings();
}
