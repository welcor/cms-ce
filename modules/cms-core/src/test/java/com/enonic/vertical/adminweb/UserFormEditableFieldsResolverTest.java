/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.adminweb;


import org.junit.Test;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.api.plugin.userstore.UserStoreConfig;
import com.enonic.cms.api.plugin.userstore.UserStoreUserFieldConfig;
import com.enonic.cms.api.plugin.userstore.UserFieldType;

import static org.junit.Assert.*;

public class UserFormEditableFieldsResolverTest
{
    private boolean canCreateUserConnectorPolicy = false;

    private boolean canUpdateUserConnectorPolicy = false;

    private UserStoreEntity userStore;

    private UserStoreConfig userStoreConfig = new UserStoreConfig();

    private UserFormEditableFieldsResolver resolver;

    @Test
    public void isEmailEditable_given_local_userStore_when_creating_then_true_is_returned()
    {
        userStore = createUserStore( false, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isEmailEditable() );
    }

    @Test
    public void isEmailEditable_given_local_userStore_when_updating_then_true_is_returned()
    {
        userStore = createUserStore( false, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.UPDATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isEmailEditable() );
    }

    @Test
    public void isEmailEditable_given_remote_userStore_and_userStore_policy_is_readOnly_when_creating_then_false_is_returned()
    {
        canCreateUserConnectorPolicy = false;
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isEmailEditable() );
    }

    @Test
    public void isEmailEditable_given_remote_userStore_and_userStore_policy_is_not_readOnly_when_creating_then_true_is_returned()
    {
        canCreateUserConnectorPolicy = true;
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isEmailEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_not_readOnly_and_userStore_is_local_when_creating_then_returns_true()
    {
        userStoreConfig.addUserFieldConfig( createLocalUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( false, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_readOnly_and_userStore_is_local_when_creating_then_returns_false()
    {
        userStoreConfig.addUserFieldConfig( createLocalReadOnlyUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( false, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_local_and_not_readOnly_when_creating_then_returns_true()
    {
        userStoreConfig.addUserFieldConfig( createLocalUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_local_and_readOnly_when_creating_then_returns_false()
    {
        userStoreConfig.addUserFieldConfig( createLocalReadOnlyUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_remote_and_not_readOnly_and_userstore_policy_is_readOnly_when_creating_then_returns_false()
    {
        canCreateUserConnectorPolicy = false;

        userStoreConfig.addUserFieldConfig( createRemoteUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_remote_and_readOnly_and_userstore_policy_is_readOnly_when_creating_then_returns_false()
    {
        canCreateUserConnectorPolicy = false;

        userStoreConfig.addUserFieldConfig( createRemoteReadOnlyUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_remote_and_not_readOnly_and_userstore_policy_is_not_readOnly_when_creating_then_returns_true()
    {
        canCreateUserConnectorPolicy = true;

        userStoreConfig.addUserFieldConfig( createRemoteUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_remote_and_not_readOnly_and_userstore_policy_is_not_readOnly_when_updating_then_returns_true()
    {
        canUpdateUserConnectorPolicy = true;

        userStoreConfig.addUserFieldConfig( createRemoteUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.UPDATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertTrue( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_remote_and_not_readOnly_and_userstore_policy_is_readOnly_when_creating_then_returns_true()
    {
        canCreateUserConnectorPolicy = false;

        userStoreConfig.addUserFieldConfig( createRemoteUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.CREATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isFirstNameEditable() );
    }

    @Test
    public void isFirstNameEditable_given_field_is_remote_and_not_readOnly_and_userstore_policy_is_readOnly_when_updating_then_returns_false()
    {
        canUpdateUserConnectorPolicy = false;

        userStoreConfig.addUserFieldConfig( createRemoteUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStore = createUserStore( true, userStoreConfig );
        resolver =
            new UserFormEditableFieldsResolver( userStore, UserFormEditableFieldsResolver.FormAction.UPDATE, canCreateUserConnectorPolicy,
                                                canUpdateUserConnectorPolicy );

        assertFalse( resolver.isFirstNameEditable() );
    }

    private UserStoreUserFieldConfig createLocalUserFieldConfig( UserFieldType type )
    {
        UserStoreUserFieldConfig userFieldConfig = new UserStoreUserFieldConfig( type );
        userFieldConfig.setRemote( false );
        userFieldConfig.setReadOnly( false );
        return userFieldConfig;
    }

    private UserStoreUserFieldConfig createLocalReadOnlyUserFieldConfig( UserFieldType type )
    {
        UserStoreUserFieldConfig userFieldConfig = new UserStoreUserFieldConfig( type );
        userFieldConfig.setRemote( false );
        userFieldConfig.setReadOnly( true );
        return userFieldConfig;
    }

    private UserStoreUserFieldConfig createRemoteUserFieldConfig( UserFieldType type )
    {
        UserStoreUserFieldConfig userFieldConfig = new UserStoreUserFieldConfig( type );
        userFieldConfig.setRemote( true );
        userFieldConfig.setReadOnly( false );
        return userFieldConfig;
    }

    private UserStoreUserFieldConfig createRemoteReadOnlyUserFieldConfig( UserFieldType type )
    {
        UserStoreUserFieldConfig userFieldConfig = new UserStoreUserFieldConfig( type );
        userFieldConfig.setRemote( true );
        userFieldConfig.setReadOnly( true );
        return userFieldConfig;
    }

    private UserStoreEntity createUserStore( boolean remote, UserStoreConfig config )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setConfig( config );
        if ( remote )
        {
            userStore.setConnectorName( "myRemoteConnector" );
        }

        assertTrue( userStore.isRemote() == remote );
        return userStore;
    }
}
