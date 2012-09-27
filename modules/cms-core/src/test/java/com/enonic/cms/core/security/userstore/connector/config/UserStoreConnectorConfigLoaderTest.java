package com.enonic.cms.core.security.userstore.connector.config;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.userstore.config.InvalidUserStoreConfigException;

import static org.junit.Assert.*;

public class UserStoreConnectorConfigLoaderTest
{
    @Test
    public void user_policy_all()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "all" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( true, config.canCreateUser() );
        assertEquals( true, config.canUpdateUser() );
        assertEquals( true, config.canUpdateUserPassword() );
        assertEquals( true, config.canDeleteUser() );
    }

    @Test
    public void user_policy_blank()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( false, config.canCreateUser() );
        assertEquals( false, config.canUpdateUser() );
        assertEquals( false, config.canUpdateUserPassword() );
        assertEquals( false, config.canDeleteUser() );
    }

    @Test
    public void user_policy_create()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "create" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( true, config.canCreateUser() );
        assertEquals( false, config.canUpdateUser() );
        assertEquals( false, config.canUpdateUserPassword() );
        assertEquals( false, config.canDeleteUser() );
    }

    @Test
    public void user_policy_update()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "update" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( false, config.canCreateUser() );
        assertEquals( true, config.canUpdateUser() );
        assertEquals( false, config.canUpdateUserPassword() );
        assertEquals( false, config.canDeleteUser() );
    }

    @Test
    public void user_policy_delete()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "delete" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( false, config.canCreateUser() );
        assertEquals( false, config.canUpdateUser() );
        assertEquals( false, config.canUpdateUserPassword() );
        assertEquals( true, config.canDeleteUser() );
    }

    @Test
    public void user_policy_all_explicit()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "create,update,delete,updatePassword" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( true, config.canCreateUser() );
        assertEquals( true, config.canUpdateUser() );
        assertEquals( true, config.canUpdateUserPassword() );
        assertEquals( true, config.canDeleteUser() );
    }

    @Test
    public void group_policy_none()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "none" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( false, config.canReadGroup() );
        assertEquals( false, config.canCreateGroup() );
        assertEquals( false, config.canUpdateGroup() );
        assertEquals( false, config.canDeleteGroup() );
        assertEquals( false, config.groupsStoredLocal() );
    }

    @Test
    public void group_policy_local()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "local" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( false, config.canReadGroup() );
        assertEquals( true, config.canCreateGroup() );
        assertEquals( true, config.canUpdateGroup() );
        assertEquals( true, config.canDeleteGroup() );
        assertEquals( true, config.groupsStoredLocal() );
    }

    @Test
    public void group_policy_all_explicit()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "read,create,update,delete" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( true, config.canReadGroup() );
        assertEquals( true, config.canCreateGroup() );
        assertEquals( true, config.canUpdateGroup() );
        assertEquals( true, config.canDeleteGroup() );
    }

    @Test
    public void group_policy_all()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "all" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( true, config.canReadGroup() );
        assertEquals( true, config.canCreateGroup() );
        assertEquals( true, config.canUpdateGroup() );
        assertEquals( true, config.canDeleteGroup() );
    }

    @Test
    public void resurrect_deleted_users()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "local" );
        properties.setProperty( "cms.userstore.connector.myConnector.resurrectDeletedUsers", "true" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( "myConnector", config.getName() );
        assertEquals( true, config.resurrectDeletedUsers() );
    }

    @Test
    public void plugin_type()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "local" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin", "generic" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( "myConnector", config.getName() );
        assertEquals( "generic", config.getPluginType() );
    }

    @Test
    public void plugin_properties()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myConnector.groupPolicy", "local" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin", "generic" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin.dialect", "oracle" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin.url", "ldap://localhost:389" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin.authDn", "cn=Directory Manager" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin.authPassword", "pass" );
        properties.setProperty( "cms.userstore.connector.myConnector.plugin.userBaseDn", "dc=example,dc=com" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        UserStoreConnectorConfig config = loader.getUserStoreConnectorConfig( "myConnector" );
        assertEquals( "myConnector", config.getName() );
        Properties pluginProperties = config.getPluginProperties();
        assertEquals( "oracle", pluginProperties.getProperty( "dialect" ) );
        assertEquals( "ldap://localhost:389", pluginProperties.getProperty( "url" ) );
        assertEquals( "cn=Directory Manager", pluginProperties.getProperty( "authDn" ) );
        assertEquals( "pass", pluginProperties.getProperty( "authPassword" ) );
        assertEquals( "dc=example,dc=com", pluginProperties.getProperty( "userBaseDn" ) );
    }

    @Test
    public void getAllUserStoreConnectorConfigs()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myOtherConnector.userPolicy", "all" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        Map<String, UserStoreConnectorConfig> configMap = loader.getAllUserStoreConnectorConfigs();
        assertEquals( 2, configMap.size() );
    }

    @Test(expected = InvalidUserStoreConfigException.class)
    public void getUserStoreConnectorConfig_throws_exception_when_connector_does_not_exist()
    {
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myConnector.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myOtherConnector.userPolicy", "all" );

        UserStoreConnectorConfigLoader loader = new UserStoreConnectorConfigLoader();
        loader.setProperties( properties );
        loader.getUserStoreConnectorConfig( "noEntry" );
    }
}
