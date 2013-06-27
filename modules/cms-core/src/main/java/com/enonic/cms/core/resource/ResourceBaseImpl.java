/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.io.Serializable;
import java.util.Calendar;

abstract class ResourceBaseImpl
    implements Serializable, ResourceBase
{
    protected final FileResourceName name;

    protected final FileResourceService service;

    public ResourceBaseImpl( FileResourceService service, FileResourceName name )
    {
        this.service = service;
        this.name = name;
    }

    public String getName()
    {
        return this.name.getName();
    }

    public boolean exists()
    {
        return this.service.getResource( this.name ) != null;
    }

    public String getPath()
    {
        return this.name.getPath();
    }

    public ResourceKey getResourceKey()
    {
        return ResourceKey.from( getPath() );
    }

    protected final FileResource ensureResource()
    {
        FileResource resource = this.service.getResource( this.name );
        if ( resource == null )
        {
            throw new IllegalStateException( "Resource [" + this.name + "] does not exist" );
        }

        return resource;
    }

    public Calendar getLastModified()
    {
        return ensureResource().getLastModified().toGregorianCalendar();
    }

    public boolean isHidden()
    {
        return this.name.isHidden();
    }
}
