package com.enonic.cms.core.search;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/6/12
 * Time: 9:52 AM
 */
public class ContentIndexSettingsBuilderTest
{
    @Test
    public void testBuildSettings()
        throws Exception
    {

        ContentIndexSettingsBuilder settingsBuilder = new ContentIndexSettingsBuilder();
        final ImmutableSettings.Builder builder = settingsBuilder.buildSettings();

        assertNotNull( builder );

    }
}
