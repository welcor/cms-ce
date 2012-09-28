package com.enonic.cms.framework.cache;

public interface CacheListener
{
    public void removeAll( CacheFacade cache );

    public void removeEntry( CacheFacade cache, String group, String key );

    public void removeGroup( CacheFacade cache, String group );

    public void removeGroupByPrefix( CacheFacade cache, String prefix );
}
