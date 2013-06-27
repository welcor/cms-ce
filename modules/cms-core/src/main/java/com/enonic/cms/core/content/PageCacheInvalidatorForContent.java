/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.portal.cache.PageCache;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;


public class PageCacheInvalidatorForContent
{
    private PageCacheService pageCacheService;

    public PageCacheInvalidatorForContent( PageCacheService pageCacheService )
    {
        this.pageCacheService = pageCacheService;
    }

    public void invalidateForContent( ContentVersionEntity version )
    {
        invalidateForContent( version.getContent() );
    }

    public void invalidateForContent( ContentEntity content )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
        ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        invalidateForContentLocations( contentLocations );
    }

    public void invalidateForContentLocations( ContentLocations contentLocations )
    {
        for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
        {
            PageCache pageCache = pageCacheService.getPageCacheService( contentLocation.getSiteKey() );
            pageCache.removeEntriesByMenuItem( contentLocation.getMenuItemKey() );

            cleanPageCache( contentLocation.getMenuItem().getParent(), pageCache );
        }
    }

    private void cleanPageCache( MenuItemEntity menuItem, PageCache pageCache )
    {
        if ( menuItem != null )
        {
            if ( menuItem.isRenderable() )
            {
                pageCache.removeEntriesByMenuItem( menuItem.getKey() );
            }
            else
            {
                cleanPageCache( menuItem.getParent(), pageCache );
            }
        }
    }
}
