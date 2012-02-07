package com.enonic.cms.core.search;

import java.io.File;

import org.elasticsearch.common.settings.Settings;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/7/12
 * Time: 1:32 PM
 */
public interface NodeSettingsBuilder
{
    public Settings createNodeSettings( File storageDir );
}
