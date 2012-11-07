package com.enonic.cms.framework.cache.base;


import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.standard.StandardCacheManager;
import com.enonic.cms.core.config.ConfigProperties;

import static junit.framework.Assert.assertEquals;

public class AbstractCacheFacadeTest
{
    private AbstractCacheManager cacheManager;

    @Before
    public void setUp()
        throws Exception
    {
        ConfigProperties props = new ConfigProperties();
        props.setProperty( "cms.cache.cache1.memoryCapacity", "10" );
        this.cacheManager = new StandardCacheManager();
        this.cacheManager.setProperties( props );
    }


    @Test
    public void testRemoveAllCount()
        throws Exception
    {
        CacheFacade cache = this.cacheManager.getOrCreateCache( "cache1" );

        assertEquals( 0, cache.getRemoveAllCount());

        cache.removeAll();

        assertEquals( 1, cache.getRemoveAllCount());
    }

    @Test
    public void testGetEffectiveness()
        throws Exception
    {
        CacheFacade cache = this.cacheManager.getOrCreateCache( "cache1" );

        cache.put( null, "key1", "value1" );
        cache.put( null, "key2", "value2" );

        assertEquals( "value1", cache.get( null, "key1" ) );
        assertEquals( "value2", cache.get( null, "key2" ) );

        assertEquals( 100, cache.getEffectiveness());

        assertEquals( null, cache.get( null, "key3" ) );

        assertEquals( 66, cache.getEffectiveness());
    }

    @Test
    public void testGetMemoryCapacityUsage()
        throws Exception
    {
        CacheFacade cache = this.cacheManager.getOrCreateCache( "cache1" );

        cache.put( null, "key1", "value1" );
        assertEquals( 10, cache.getMemoryCapacityUsage());

        cache.put( null, "key2", "value2" );
        assertEquals( 20, cache.getMemoryCapacityUsage());
    }

    @Test
    public void testRemoveAllCountClearsStatistics()
        throws Exception
    {
        CacheFacade cache = this.cacheManager.getOrCreateCache( "cache1" );

        cache.put( null, "key1", "value1" );
        cache.put( null, "key2", "value2" );
        assertEquals( "value1", cache.get( null, "key1" ) );
        assertEquals( null, cache.get( null, "key3" ) );

        assertEquals( 1, cache.getMissCount());
        assertEquals( 1, cache.getHitCount());

        cache.removeAll();

        assertEquals( 0, cache.getMissCount());
        assertEquals( 0, cache.getHitCount());
    }

}
