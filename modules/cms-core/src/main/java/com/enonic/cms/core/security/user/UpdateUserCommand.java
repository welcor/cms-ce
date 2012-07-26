/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.group.AbstractMembershipsCommand;
import com.enonic.cms.core.user.field.UserFields;


public class UpdateUserCommand
    extends AbstractMembershipsCommand
{
    private UserKey updater;

    private UserSpecification specification;

    private String displayName;

    private String email;

    private String syncValue;

    private UserType type = UserType.NORMAL;

    private boolean syncMemberships = false;

    private boolean updateOpenGroupsOnly = false;

    private boolean allowUpdateSelf = false;

    private UpdateStrategy updateStrategy = UpdateStrategy.REPLACE_ALL;

    private UserFields userFields = new UserFields();

    private boolean removePhoto = false;

    private enum UpdateStrategy
    {
        REPLACE_ALL,
        REPLACE_NEW
    }

    public UpdateUserCommand( UserKey updater, UserSpecification specification )
    {
        this.updater = updater;
        this.specification = specification;
    }

    public UpdateUserCommand( UserSpecification specification )
    {
        this.specification = specification;
    }

    public UserSpecification getSpecification()
    {
        return specification;
    }

    public UserKey getUpdater()
    {
        return updater;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String value )
    {
        displayName = value;
    }

    public UserType getType()
    {
        return type;
    }

    public void setType( UserType type )
    {
        this.type = type;
    }

    public boolean syncMemberships()
    {
        return syncMemberships;
    }

    public void setSyncMemberships( final boolean value )
    {
        syncMemberships = value;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getSyncValue()
    {
        return syncValue;
    }

    public void setSyncValue( String syncValue )
    {
        this.syncValue = syncValue;
    }

    public boolean isModifyStrategy()
    {
        return UpdateStrategy.REPLACE_NEW.equals( updateStrategy );
    }

    public boolean isUpdateStrategy()
    {
        return UpdateStrategy.REPLACE_ALL.equals( updateStrategy );
    }

    public void setupUpdateStrategy()
    {
        this.updateStrategy = UpdateStrategy.REPLACE_ALL;
    }

    public void setupModifyStrategy()
    {
        this.updateStrategy = UpdateStrategy.REPLACE_NEW;
    }

    public boolean isUpdateOpenGroupsOnly()
    {
        return updateOpenGroupsOnly;
    }

    public void setUpdateOpenGroupsOnly( boolean updateOpenGroupsOnly )
    {
        this.updateOpenGroupsOnly = updateOpenGroupsOnly;
    }

    public UserFields getUserFields()
    {
        return userFields;
    }

    public void setUserFields( UserFields userFields )
    {
        this.userFields = userFields;
    }

    public boolean removePhoto()
    {
        return removePhoto;
    }

    public void setRemovePhoto( final boolean value )
    {
        removePhoto = value;
    }

    public boolean allowUpdateSelf()
    {
        return allowUpdateSelf;
    }

    public void setAllowUpdateSelf( boolean allowUpdateSelf )
    {
        this.allowUpdateSelf = allowUpdateSelf;
    }
}
