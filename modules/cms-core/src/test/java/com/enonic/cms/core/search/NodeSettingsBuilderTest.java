package com.enonic.cms.core.search;

import java.io.File;

import org.elasticsearch.common.settings.Settings;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/6/12
 * Time: 9:32 AM
 */
public class NodeSettingsBuilderTest
{


    @Before
    public void setUp()
    {

    }

    @Test
    public void testCreateSettings()
    {
        final Settings nodeSettings = NodeSettingsBuilder.createNodeSettings( new File( "test" ) );

        assertNotNull( nodeSettings );
    }


}
