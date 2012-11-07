package com.enonic.cms.core.portal.livetrace.systeminfo;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

public class SystemInfoFactory
{
    private ThreadMXBean threadMXBean;

    private MemoryMXBean memoryMXBean;

    public SystemInfoFactory()
    {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    public SystemInfo createSystemInfo( final int numberOfPortalRequestsInProgress, final CacheManager cacheManager )
    {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setPortalRequestInProgress( numberOfPortalRequestsInProgress );
        systemInfo.setPageCacheStatistic( createPageCacheStatistic( cacheManager.getOrCreateCache( ( "page" ) ) ) );
        systemInfo.setEntityCacheStatistic( createPageCacheStatistic( cacheManager.getOrCreateCache( "entity" ) ) );

        systemInfo.setJavaHeapMemoryStatistic( createJavaMemoryStatistic( memoryMXBean.getHeapMemoryUsage() ) );
        systemInfo.setJavaNonHeapMemoryStatistic( createJavaMemoryStatistic( memoryMXBean.getNonHeapMemoryUsage() ) );
        systemInfo.setJavaThreadStatistic( createJavaThreadStatistic() );
        return systemInfo;
    }

    private JavaThreadStatistic createJavaThreadStatistic()
    {
        JavaThreadStatistic javaThreadStatistic = new JavaThreadStatistic();
        javaThreadStatistic.setCount( threadMXBean.getThreadCount() );
        javaThreadStatistic.setPeakCount( threadMXBean.getPeakThreadCount() );
        return javaThreadStatistic;
    }

    private JavaMemoryStatistic createJavaMemoryStatistic( MemoryUsage memoryUsage )
    {
        JavaMemoryStatistic javaMemoryStatistic = new JavaMemoryStatistic();
        javaMemoryStatistic.setInit( memoryUsage.getInit() );
        javaMemoryStatistic.setUsed( memoryUsage.getUsed() );
        javaMemoryStatistic.setCommitted( memoryUsage.getCommitted() );
        javaMemoryStatistic.setMax( memoryUsage.getMax() );
        return javaMemoryStatistic;
    }

    private CacheStatistic createPageCacheStatistic( final CacheFacade cache )
    {
        CacheStatistic cacheStatistic = new CacheStatistic();
        cacheStatistic.setCapacity( cache.getMemoryCapacity() );
        cacheStatistic.setCount( cache.getCount() );
        cacheStatistic.setHitCount( cache.getHitCount() );
        cacheStatistic.setMissCount( cache.getMissCount() );
        cacheStatistic.setRemoveAllCount( cache.getRemoveAllCount() );
        return cacheStatistic;
    }
}

