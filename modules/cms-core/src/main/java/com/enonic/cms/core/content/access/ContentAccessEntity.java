/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.access;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.ContentAccessControl;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.security.group.GroupEntity;

public class ContentAccessEntity
    implements Serializable
{

    private String key;

    private Integer readAccess;

    private Integer updateAccess;

    private Integer deleteAccess;

    private GroupEntity group;

    private ContentEntity content;

    public ContentAccessEntity()
    {
        readAccess = 0;
        updateAccess = 0;
        deleteAccess = 0;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public Boolean isReadAccess()
    {
        return readAccess != null && readAccess == 1;
    }

    public boolean isUpdateAccess()
    {
        return updateAccess != null && updateAccess == 1;
    }

    public boolean isDeleteAccess()
    {
        return deleteAccess != null && deleteAccess == 1;
    }

    public GroupEntity getGroup()
    {
        return group;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public void setReadAccess( boolean readAccess )
    {
        this.readAccess = readAccess ? 1 : 0;
    }

    public void setUpdateAccess( boolean updateAccess )
    {
        this.updateAccess = updateAccess ? 1 : 0;
    }

    public void setDeleteAccess( boolean deleteAccess )
    {
        this.deleteAccess = deleteAccess ? 1 : 0;
    }

    public void setGroup( GroupEntity group )
    {
        this.group = group;
    }

    public void setContent( ContentEntity value )
    {
        this.content = value;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ContentAccessEntity ) )
        {
            return false;
        }

        ContentAccessEntity that = (ContentAccessEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 173, 153 ).append( key ).toHashCode();
    }

    public boolean overwriteRightsFrom( ContentAccessControl other )
    {
        boolean modified = false;
        if ( isReadAccess() != other.isRead() )
        {
            setReadAccess( other.isRead() );
            modified = true;
        }
        if ( isUpdateAccess() != other.isUpdate() )
        {
            setUpdateAccess( other.isUpdate() );
            modified = true;
        }
        if ( isDeleteAccess() != other.isDelete() )
        {
            setDeleteAccess( other.isDelete() );
            modified = true;
        }

        return modified;
    }

    public ContentAccessEntity copy()
    {
        ContentAccessEntity copy = new ContentAccessEntity();
        copy.readAccess = this.readAccess;
        copy.updateAccess = this.updateAccess;
        copy.deleteAccess = this.deleteAccess;
        copy.group = this.group;
        return copy;
    }

}
