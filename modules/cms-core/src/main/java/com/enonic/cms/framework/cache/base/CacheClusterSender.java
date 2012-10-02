package com.enonic.cms.framework.cache.base;

public interface CacheClusterSender
{
    public void sendEvictMessage( String cacheName, String objectKey );

    public void sendEvictGroupMessage( String cacheName, String groupName );

    public void sendEvictByGroupPrefixMessage( String cacheName, String groupPrefix );

    public void sendEvictAllMessage( String cacheName );
}
