package com.enonic.cms.framework.cache.base;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CacheManagerConfigTest
{
    private CacheManagerConfig managerConfig;

    @Before
    public void setUp()
    {
        final Properties props = new Properties();
        props.setProperty( "cms.cache.entity.memoryCapacity", "100" );
        props.setProperty( "cms.cache.entity.timeToLive", "0" );

        this.managerConfig = new CacheManagerConfig( props );
    }

    @Test
    public void testDefaultConfig()
    {
        final CacheConfig config = this.managerConfig.getCacheConfig( "unknown" );

        assertNotNull( config );
        assertEquals( 1000, config.getMemoryCapacity() );
        assertEquals( 0, config.getTimeToLive() );
    }

    @Test
    public void testCacheConfig()
    {
        final CacheConfig config = this.managerConfig.getCacheConfig( "entity" );

        assertNotNull( config );
        assertEquals( 100, config.getMemoryCapacity() );
        assertEquals( 0, config.getTimeToLive() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCacheConfig_IllegalArgumentException_thrown_when_entity_cache_timeToLive_is_other_than_zero()
    {
        final Properties props = new Properties();
        props.setProperty( "cms.cache.entity.memoryCapacity", "100" );
        props.setProperty( "cms.cache.entity.timeToLive", "100" );
        this.managerConfig = new CacheManagerConfig( props );

        // exercise
        this.managerConfig.getCacheConfig( "entity" );
    }
}
