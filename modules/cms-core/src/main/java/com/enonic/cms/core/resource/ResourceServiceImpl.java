/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Multimap;

import com.enonic.cms.store.dao.ResourceUsageDao;

@Component("resourceService")
public class ResourceServiceImpl
    implements ResourceService
{
    @Autowired
    private ResourceUsageDao resourceUsageDao;

    @Autowired
    private FileResourceService fileResourceService;

    public ResourceFolder getResourceRoot()
    {
        return doGetResourceRoot();
    }

    public ResourceFile getResourceFile( ResourceKey resourceKey )
    {
        if ( resourceKey == null )
        {
            throw new IllegalArgumentException( "Given resourceKey cannot be null" );
        }
        return doGetResourceRoot().getFile( resourceKey.toString() );
    }

    public ResourceFolder getResourceFolder( ResourceKey resourceKey )
    {
        if ( resourceKey == null )
        {
            throw new IllegalArgumentException( "Given resourceKey cannot be null" );
        }
        return doGetResourceRoot().getFolder( resourceKey.toString() );
    }

    public ResourceBase getResource( ResourceKey resourceKey )
    {
        ResourceBase resource = getResourceFile( resourceKey );
        if ( resource == null )
        {
            resource = getResourceFolder( resourceKey );
        }
        return resource;
    }

    public HashMap<ResourceKey, Long> getUsageCountMap()
    {
        return resourceUsageDao.getUsageCountMap();
    }

    public Multimap<ResourceKey, ResourceReferencer> getUsedBy( ResourceKey resourceKey )
    {
        return resourceUsageDao.getUsedBy( resourceKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public ResourceKey moveResource( ResourceBase source, ResourceFolder destination )
    {

        if ( source == null )
        {
            throw new IllegalArgumentException( "Resource destination cannot be null" );
        }

        if ( source instanceof ResourceFile )
        {
            ResourceKey newResourceKey = ResourceKey.from( destination.getResourceKey() + "/" + source.getName() );
            resourceUsageDao.updateResourceReference(source.getResourceKey(), newResourceKey);
        }
        else if ( source instanceof ResourceFolder )
        {
            String oldPrefix = source.getPath() + "/";
            String newPrefix = destination.getPath() + "/" + source.getName() + "/";
            resourceUsageDao.updateResourceReferencePrefix(oldPrefix, newPrefix);
        }
        else
        {
            throw new IllegalArgumentException(
                "Resource must be of type ResourceFile or ResourceFolder, was: " + source.getClass().getName() );
        }

        return moveTo( source, destination );
    }

    private ResourceKey moveTo( ResourceBase source, ResourceFolder destinationFolder )
    {
        if ( destinationFolder == null )
        {
            throw new IllegalArgumentException( "Destination cannot be null" );
        }
        else if ( !( destinationFolder instanceof ResourceFolderImpl ) )
        {
            throw new IllegalArgumentException( "Destination '" + destinationFolder.getResourceKey() + "' must be a folder" );
        }

        boolean exists = ( (ResourceBaseImpl) destinationFolder ).exists();
        if ( !exists )
        {
            throw new IllegalArgumentException( "Destination '" + destinationFolder.getResourceKey() + "' does not exist" );
        }

        FileResourceName srcName = new FileResourceName( source.getPath() );
        FileResourceName destName = new FileResourceName( ( (ResourceBaseImpl) destinationFolder ).name, source.getName() );
        this.fileResourceService.moveResource( srcName, destName );
        return ResourceKey.from( ( (ResourceBaseImpl) destinationFolder ).name.getPath() );
    }

    private ResourceFolder doGetResourceRoot()
    {
        return new ResourceFolderImpl( this.fileResourceService, new FileResourceName( "/" ) );
    }
}
