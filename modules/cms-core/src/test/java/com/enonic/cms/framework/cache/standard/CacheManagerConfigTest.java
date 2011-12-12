package com.enonic.cms.framework.cache.standard;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CacheManagerConfigTest
{
    private CacheManagerConfig managerConfig;

    @Before
    public void setUp()
    {
        final Properties props = new Properties();
        props.setProperty( "cms.cache.entity.memoryCapacity", "100" );
        props.setProperty( "cms.cache.entity.timeToLive", "10" );

        this.managerConfig = new CacheManagerConfig( props );
    }

    @Test
    public void testDefaultConfig()
    {
        final CacheConfig config = this.managerConfig.getCacheConfig( "unknown" );

        assertNotNull(config);
        assertEquals( 1000, config.getMemoryCapacity() );
        assertEquals( 0, config.getTimeToLive() );
    }

    @Test
    public void testCacheConfig()
    {
        final CacheConfig config = this.managerConfig.getCacheConfig( "entity" );

        assertNotNull(config);
        assertEquals( 100, config.getMemoryCapacity() );
        assertEquals( 10, config.getTimeToLive() );
    }
}
