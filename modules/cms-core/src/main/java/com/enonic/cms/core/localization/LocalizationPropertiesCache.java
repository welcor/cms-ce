/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.localization;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.resource.ResourceBase;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;

final class LocalizationPropertiesCache
{
    private final CacheFacade cacheFacade;

    private final ResourceService resourceService;

    private final long checkInterval;

    public LocalizationPropertiesCache( final CacheFacade cacheFacade, final ResourceService resourceService, final long checkInterval )
    {
        this.cacheFacade = cacheFacade;
        this.resourceService = resourceService;
        this.checkInterval = checkInterval;
    }

    public LocalizationPropertiesCacheEntry get( final ResourceKey key )
    {
        final LocalizationPropertiesCacheEntry entry = (LocalizationPropertiesCacheEntry) this.cacheFacade.get( null, key.toString() );
        if ( entry == null )
        {
            return null;
        }

        if ( isValid( entry ) )
        {
            return entry;
        }

        this.cacheFacade.remove( null, key.toString() );
        return null;
    }

    public void put( final LocalizationPropertiesCacheEntry entry )
    {
        this.cacheFacade.put( null, entry.getKey().toString(), entry );
    }

    public boolean isValid( final LocalizationPropertiesCacheEntry entry )
    {
        final long now = System.currentTimeMillis();
        if ( ( now - entry.getLastValidated() ) < this.checkInterval )
        {
            return true;
        }

        entry.setLastValidated( now );
        return !isModifiedAfter( entry.getKey(), entry.getTimestamp() );
    }

    private boolean isModifiedAfter( final ResourceKey key, final long timestamp )
    {
        final ResourceBase resource = this.resourceService.getResource( key );
        return ( resource == null ) || ( resource.getLastModified().getTimeInMillis() > timestamp );
    }
}
