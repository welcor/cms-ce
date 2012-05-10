/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.group.AbstractMembershipsCommand;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldMap;
import com.enonic.cms.core.user.field.UserInfoTransformer;
import com.enonic.cms.core.user.remote.RemoteUser;


public class UpdateUserCommand
    extends AbstractMembershipsCommand
{
    private UserKey updater;

    private UserSpecification specification;

    private String password;

    private String displayName;

    private String email;

    private String syncValue;

    private UserType type = UserType.NORMAL;

    private boolean syncMemberships = false;

    private boolean updateOpenGroupsOnly = false;

    private boolean allowUpdateSelf = false;

    private UpdateStrategy updateStrategy = UpdateStrategy.REPLACE_ALL;

    private UserInfo userInfo = new UserInfo();

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

    public UserFieldMap getUserFields()
    {
        return new UserInfoTransformer().toUserFields( getUserInfo() );
    }

    /**
     * Picks out and returns all the fields in the command that are different from the fields of a remote user.
     *
     * @param remoteUser The object of the remote user whose fields will be compared.
     * @return A set of all fields that are different from or does not exist in the <code>currentUserFields</code>.
     */
    public UserFieldMap getChangedFields( RemoteUser remoteUser )
    {
        final UserFieldMap commandUserFields = getUserFields();
        UserFieldMap remoteUserFields = remoteUser.getUserFields();

        final UserFieldMap changedCommandFields = new UserFieldMap( true );

        for ( UserField commandField : commandUserFields )
        {
            UserField remoteField = remoteUserFields.getField( commandField.getType() );
            if (commandField.compareTo( remoteField ) != 0) {
                changedCommandFields.add( commandField );
            }
        }
        if ( isUpdateStrategy() )
        {
            for ( UserField remoteField : remoteUserFields )
            {
                UserField commandFieldMatchingRemote = commandUserFields.getField( remoteField.getType() );
                if ( commandFieldMatchingRemote == null )
                {
                    changedCommandFields.add( new UserField( remoteField.getType(), null ) );
                }
            }
        }
        return changedCommandFields;
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
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

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( final UserInfo value )
    {
        userInfo = value;
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
