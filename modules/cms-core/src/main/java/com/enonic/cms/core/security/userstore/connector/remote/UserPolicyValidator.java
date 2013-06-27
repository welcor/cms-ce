/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.userstore.connector.remote;


import java.util.Collection;

import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.userstore.UserStoreConnectorPolicyBrokenException;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.core.user.remote.RemoteUser;

public class UserPolicyValidator
{
    private UserStoreConnectorConfig connectorConfig;

    private UserStoreEntity userStore;

    public UserPolicyValidator( UserStoreConnectorConfig connectorConfig, UserStoreEntity userStore )
    {
        this.connectorConfig = connectorConfig;
        this.userStore = userStore;
    }

    public void validateFieldsForUpdate( final UpdateUserCommand command, final RemoteUser remoteUser )
    {
        if ( connectorConfig.canUpdateUser() )
        {
            return;
        }

        if ( command.getEmail() != null || command.isUpdateStrategy() )
        {
            if ( !equals( remoteUser.getEmail(), command.getEmail() ) )
            {
                throw new UserStoreConnectorPolicyBrokenException( userStore.getName(), userStore.getConnectorName(),
                                                                   "Trying to update email on a user store without 'update' policy." );
            }
        }

        // includeMissing: treats a missing field in command also as a change
        final boolean includeMissing = command.isUpdateStrategy();
        final UserFields commandUserFields;
        if ( includeMissing )
        {
            commandUserFields = command.getUserFields().getConfiguredFieldsOnly( userStore.getConfig(), true );
        }
        else
        {
            commandUserFields = command.getUserFields().getConfiguredFieldsOnly( userStore.getConfig(), false );
        }

        final UserFields remoteUserFields = remoteUser.getUserFields().getConfiguredFieldsOnly( userStore.getConfig() );

        final UserFields changedUserFields = commandUserFields.getChangedUserFields( remoteUserFields, includeMissing );
        final UserFields changedUserFieldsRemoteOnly = changedUserFields.getRemoteFields( userStore.getConfig() );
        if ( changedUserFieldsRemoteOnly.getSize() == 0 )
        {
            return;
        }

        final StringBuilder remoteFields = new StringBuilder();
        Collection<UserField> remainingRemoteFields = changedUserFieldsRemoteOnly.getAll();
        for ( UserField userField : remainingRemoteFields )
        {
            remoteFields.append( userField.getType().getName() ).append( ", " );
        }

        throw new UserStoreConnectorPolicyBrokenException( userStore.getName(), userStore.getConnectorName(),
                                                           "Trying to update remote fields on a user store without 'update' policy. The fields are: " +
                                                               remoteFields.toString() );
    }

    private boolean equals( Object a, Object b )
    {
        if ( a == null && b == null )
        {
            return true;
        }
        else if ( a == null || b == null )
        {
            return false;
        }
        return a.equals( b );
    }
}
