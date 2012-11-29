package com.enonic.cms.core.portal.livetrace.systeminfo;

import org.joda.time.DateTime;

public class SystemInfo
{
    private DateTime systemTime;

    private String systemUpTime;

    private int portalRequestInProgress;

    private JavaThreadStatistic javaThreadStatistic;

    private JavaMemoryStatistic javaHeapMemoryStatistic;

    private JavaMemoryStatistic javaNonHeapMemoryStatistic;

    private CacheStatistic pageCacheStatistic;

    private CacheStatistic entityCacheStatistic;

    private CacheStatistic xsltCacheStatistic;

    @SuppressWarnings("UnusedDeclaration")
    public DateTime getSystemTime()
    {
        return systemTime;
    }

    public void setSystemTime( final DateTime systemTime )
    {
        this.systemTime = systemTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSystemUpTime()
    {
        return systemUpTime;
    }

    public void setSystemUpTime( final String systemUpTime )
    {
        this.systemUpTime = systemUpTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getPortalRequestInProgress()
    {
        return portalRequestInProgress;
    }

    void setPortalRequestInProgress( int portalRequestInProgress )
    {
        this.portalRequestInProgress = portalRequestInProgress;
    }

    @SuppressWarnings("UnusedDeclaration")
    public JavaThreadStatistic getJavaThreadStatistic()
    {
        return javaThreadStatistic;
    }

    void setJavaThreadStatistic( JavaThreadStatistic javaThreadStatistic )
    {
        this.javaThreadStatistic = javaThreadStatistic;
    }

    @SuppressWarnings("UnusedDeclaration")
    public JavaMemoryStatistic getJavaHeapMemoryStatistic()
    {
        return javaHeapMemoryStatistic;
    }

    void setJavaHeapMemoryStatistic( JavaMemoryStatistic javaHeapMemoryStatistic )
    {
        this.javaHeapMemoryStatistic = javaHeapMemoryStatistic;
    }

    @SuppressWarnings("UnusedDeclaration")
    public JavaMemoryStatistic getJavaNonHeapMemoryStatistic()
    {
        return javaNonHeapMemoryStatistic;
    }

    void setJavaNonHeapMemoryStatistic( JavaMemoryStatistic javaNonHeapMemoryStatistic )
    {
        this.javaNonHeapMemoryStatistic = javaNonHeapMemoryStatistic;
    }

    @SuppressWarnings("UnusedDeclaration")
    public CacheStatistic getPageCacheStatistic()
    {
        return pageCacheStatistic;
    }

    void setPageCacheStatistic( CacheStatistic pageCacheStatistic )
    {
        this.pageCacheStatistic = pageCacheStatistic;
    }

    @SuppressWarnings("UnusedDeclaration")
    public CacheStatistic getEntityCacheStatistic()
    {
        return entityCacheStatistic;
    }

    void setEntityCacheStatistic( CacheStatistic entityCacheStatistic )
    {
        this.entityCacheStatistic = entityCacheStatistic;
    }

    @SuppressWarnings("UnusedDeclaration")
    public CacheStatistic getXsltCacheStatistic()
    {
        return xsltCacheStatistic;
    }

    void setXsltCacheStatistic( CacheStatistic xsltCacheStatistic )
    {
        this.xsltCacheStatistic = xsltCacheStatistic;
    }
}
