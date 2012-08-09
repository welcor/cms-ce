package com.enonic.cms.core.content;


import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessControl;
import com.enonic.cms.core.security.group.GroupKey;

public class ContentAccessControl
{
    private GroupKey group;

    private boolean read = false;

    private boolean update = false;

    private boolean delete = false;

    public GroupKey getGroup()
    {
        return group;
    }

    public boolean isRead()
    {
        return read;
    }

    public boolean isUpdate()
    {
        return update;
    }

    public boolean isDelete()
    {
        return delete;
    }

    public void setGroup( GroupKey group )
    {
        this.group = group;
    }

    public void setRead( boolean read )
    {
        this.read = read;
    }

    public void setUpdate( boolean update )
    {
        this.update = update;
    }

    public void setDelete( boolean delete )
    {
        this.delete = delete;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append( group ).append( " -> " );
        s.append( "read=" ).append( read );
        s.append( ", update=" ).append( update );
        s.append( ", delete=" ).append( delete );
        return s.toString();
    }

    public static ContentAccessControl create( ContentAccessEntity value )
    {
        ContentAccessControl control = new ContentAccessControl();
        control.group = value.getGroup().getGroupKey();
        control.read = value.isReadAccess();
        control.update = value.isUpdateAccess();
        control.delete = value.isDeleteAccess();
        return control;
    }

    public static ContentAccessControl create( CategoryAccessControl car )
    {
        ContentAccessControl control = new ContentAccessControl();
        control.group = car.getGroupKey();
        control.read = car.givesContentRead();
        control.update = car.givesContentUpdate();
        control.delete = car.givesContentDelete();
        return control;
    }
}
