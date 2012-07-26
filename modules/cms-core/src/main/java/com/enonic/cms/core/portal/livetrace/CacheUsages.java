package com.enonic.cms.core.portal.livetrace;

import java.util.ArrayList;
import java.util.List;

class CacheUsages
{
    private List<CacheUsage> cacheUsages = new ArrayList<CacheUsage>();

    void add( CacheUsage cacheUsage )
    {
        this.cacheUsages.add( cacheUsage );
    }

    void add( CacheUsages cacheUsages )
    {
        for ( CacheUsage cacheUsage : cacheUsages.cacheUsages )
        {
            this.cacheUsages.add( cacheUsage );
        }
    }

    public List<CacheUsage> getList()
    {
        return cacheUsages;
    }
}
