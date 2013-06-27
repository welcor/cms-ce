/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace.systeminfo;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.time.TimeService;

@Component
public class SystemInfoFactory
{
    @Autowired
    private TimeService timeService;

    private ThreadMXBean threadMXBean;

    private MemoryMXBean memoryMXBean;

    private static final PeriodFormatter UP_TIME_PERIOD_FORMATTER =
        new PeriodFormatterBuilder().minimumPrintedDigits( 2 ).appendDays().appendSuffix( "d" ).appendSeparatorIfFieldsAfter(
            " " ).appendHours().appendSuffix( "h" ).appendSeparatorIfFieldsAfter( " " ).appendMinutes().appendSuffix(
            "m" ).appendSeparatorIfFieldsAfter( " " ).appendSeconds().appendSuffix( "s" ).toFormatter();

    public SystemInfoFactory()
    {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    public SystemInfo createSystemInfo( final int numberOfPortalRequestsInProgress, final CacheManager cacheManager )
    {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setSystemTime( timeService.getNowAsDateTime() );
        systemInfo.setSystemUpTime( UP_TIME_PERIOD_FORMATTER.print( timeService.upTime() ) );
        systemInfo.setPortalRequestInProgress( numberOfPortalRequestsInProgress );
        systemInfo.setPageCacheStatistic( createCacheStatistic( cacheManager.getPageCache() ) );
        systemInfo.setEntityCacheStatistic( createCacheStatistic( cacheManager.getEntityCache() ) );
        systemInfo.setXsltCacheStatistic( createCacheStatistic( cacheManager.getXsltCache() ) );

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

    private CacheStatistic createCacheStatistic( final CacheFacade cache )
    {
        CacheStatistic cacheStatistic = new CacheStatistic();
        cacheStatistic.setCapacity( cache.getMemoryCapacity() );
        cacheStatistic.setCount( cache.getCount() );
        cacheStatistic.setHitCount( cache.getHitCount() );
        cacheStatistic.setMissCount( cache.getMissCount() );
        cacheStatistic.setRemoveAllCount( cache.getRemoveAllCount() );
        cacheStatistic.setMemoryCapacityUsage( cache.getMemoryCapacityUsage() );
        cacheStatistic.setEffectiveness( cache.getEffectiveness() );
        return cacheStatistic;
    }
}

