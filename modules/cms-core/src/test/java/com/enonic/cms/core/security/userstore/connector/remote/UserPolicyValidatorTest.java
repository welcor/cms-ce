/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.userstore.connector.remote;

import org.junit.Test;

import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.api.plugin.userstore.RemoteUser;

public class UserPolicyValidatorTest
{
    @Test
    public void asdfasdf()
    {
        UserPolicyConfig userPolicy = new UserPolicyConfig( "user", "create" );
        GroupPolicyConfig groupPolicy = new GroupPolicyConfig( "group", "all" );
        UserStoreConnectorConfig connectorConfig = new UserStoreConnectorConfig( "myConnector", "ldap", userPolicy, groupPolicy );
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( "MyUserStore" );
        userStore.setConnectorName( "myConnector" );
        UserPolicyValidator validator = new UserPolicyValidator( connectorConfig, userStore );

        UpdateUserCommand updateUserCommand = new UpdateUserCommand( null, null );
        RemoteUser remoteUser = new RemoteUser( "uid" );

        // exercise
        validator.validateFieldsForUpdate( updateUserCommand, remoteUser );
    }
}
