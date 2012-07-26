package com.enonic.cms.core.portal.livetrace.systeminfo;

public class SystemInfo
{
    private int portalRequestInProgress;

    private JavaThreadStatistic javaThreadStatistic;

    private JavaMemoryStatistic javaHeapMemoryStatistic;

    private JavaMemoryStatistic javaNonHeapMemoryStatistic;

    private CacheStatistic pageCacheStatistic;

    private CacheStatistic entityCacheStatistic;

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
}
