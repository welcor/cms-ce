package com.enonic.cms.framework.cache.standard;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheListener;

final class NopCacheListener
    implements CacheListener
{
    @Override
    public void removeAll( final CacheFacade cache )
    {
    }

    @Override
    public void removeEntry( final CacheFacade cache, final String group, final String key )
    {
    }

    @Override
    public void removeGroup( final CacheFacade cache, final String group )
    {
    }

    @Override
    public void removeGroupByPrefix( final CacheFacade cache, final String prefix )
    {
    }
}
