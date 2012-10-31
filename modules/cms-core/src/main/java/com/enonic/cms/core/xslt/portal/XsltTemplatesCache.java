package com.enonic.cms.core.xslt.portal;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.FileResourceService;

final class XsltTemplatesCache
{
    private final CacheFacade cacheFacade;

    private final FileResourceService resourceService;

    private final long checkInterval;

    public XsltTemplatesCache( final CacheFacade cacheFacade, final FileResourceService resourceService, final long checkInterval )
    {
        this.cacheFacade = cacheFacade;
        this.resourceService = resourceService;
        this.checkInterval = checkInterval;
    }

    public XsltTemplatesCacheEntry get( final FileResourceName name )
    {
        final XsltTemplatesCacheEntry entry = (XsltTemplatesCacheEntry) this.cacheFacade.get( null, name.toString() );
        if ( entry == null )
        {
            return null;
        }

        if ( isValid( entry ) )
        {
            return entry;
        }
        else
        {
            this.cacheFacade.remove( null, name.toString() );
            return null;
        }
    }

    public void put( final XsltTemplatesCacheEntry entry )
    {
        this.cacheFacade.put( null, entry.getName().toString(), entry );
    }

    public boolean isValid( final XsltTemplatesCacheEntry entry )
    {
        final long now = System.currentTimeMillis();
        if ( ( now - entry.getLastValidated() ) < this.checkInterval )
        {
            return true;
        }

        entry.setLastValidated( now );
        for ( final FileResourceName name : entry.getResourceSet() )
        {
            if ( isModifiedAfter( name, entry.getCompileTimestamp() ) )
            {
                return false;
            }
        }

        return true;
    }

    private boolean isModifiedAfter( final FileResourceName name, final long timestamp )
    {
        final FileResource resource = this.resourceService.getResource( name );
        return ( resource == null ) || ( resource.getLastModified().getMillis() > timestamp );
    }
}
