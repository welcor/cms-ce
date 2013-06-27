/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.ArrayList;
import java.util.List;

public final class ResourceFolderImpl
    extends ResourceBaseImpl
    implements ResourceFolder
{
    public ResourceFolderImpl( FileResourceService service, FileResourceName name )
    {
        super( service, name );
    }

    public ResourceFolder getFolder( String name )
    {
        FileResource res = this.service.getResource( new FileResourceName( this.name, name ) );
        if ( ( res != null ) && res.isFolder() )
        {
            return new ResourceFolderImpl( this.service, res.getName() );
        }

        return null;
    }

    public ResourceFile getFile( String name )
    {
        FileResource res = this.service.getResource( new FileResourceName( this.name, name ) );
        if ( ( res != null ) && !res.isFolder() )
        {
            return new ResourceFileImpl( this.service, res.getName() );
        }

        return null;
    }

    public List<ResourceFolder> getFolders()
    {
        List<ResourceFolder> folders = new ArrayList<ResourceFolder>();
        for ( FileResourceName child : this.service.getChildren( this.name ) )
        {
            FileResource res = this.service.getResource( child );
            if ( res != null && res.isFolder() )
            {
                folders.add( new ResourceFolderImpl( this.service, res.getName() ) );
            }
        }

        return folders;
    }

    public List<ResourceFile> getFiles()
    {
        List<ResourceFile> files = new ArrayList<ResourceFile>();
        for ( FileResourceName child : this.service.getChildren( this.name ) )
        {
            FileResource res = this.service.getResource( child );
            if ( res != null && !res.isFolder() )
            {
                files.add( new ResourceFileImpl( this.service, res.getName() ) );
            }
        }

        return files;
    }
}
