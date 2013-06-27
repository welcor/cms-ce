/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.PropertiesUtil;

import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.userstore.config.InvalidUserStoreConfigException;

@Component
public class UserStoreConnectorConfigLoader
{
    private Properties properties;

    private Set<String> userStoreConnectorNames = null;

    public Map<String, UserStoreConnectorConfig> getAllUserStoreConnectorConfigs()
    {
        Collection<String> allNames = doGetAllUserStoreConnectorNames();
        Map<String, UserStoreConnectorConfig> configs = new LinkedHashMap<String, UserStoreConnectorConfig>( allNames.size() );

        for ( final String name : allNames )
        {
            configs.put( name, doGetUserStoreConnectorConfig( name, true ) );
        }

        return configs;
    }

    public UserStoreConnectorConfig getUserStoreConnectorConfig( final String connectorName )
    {
        return doGetUserStoreConnectorConfig( connectorName, false );
    }

    private UserStoreConnectorConfig doGetUserStoreConnectorConfig( final String connectorName, final boolean failSilent )
    {
        if ( !doGetAllUserStoreConnectorNames().contains( connectorName ) )
        {
            //FIXME localization impossible, message shown directly in GUI
            final String errorMessage =
                InvalidUserStoreConnectorConfigException.createMessage( connectorName, "No configuration found in cms.properties" );
            if ( failSilent )
            {
                final UserStoreConnectorConfig config =
                    new UserStoreConnectorConfig( connectorName, null, UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE );
                config.addErrorMessage( errorMessage );
                return config;
            }
            else
            {
                throw new InvalidUserStoreConfigException( errorMessage );
            }
        }

        return doLoadConfig( connectorName );
    }

    private UserStoreConnectorConfig doLoadConfig( final String connectorName )
    {
        UserPolicyConfig userPolicyConfig;
        String userPolicyConfigErrorMessage = null;
        try
        {
            userPolicyConfig = getUserPolicy( connectorName );
        }
        catch ( InvalidUserStoreConnectorConfigException e )
        {
            userPolicyConfigErrorMessage = e.getMessage();
            userPolicyConfig = UserPolicyConfig.ALL_FALSE;
        }

        GroupPolicyConfig groupPolicyConfig;
        String groupPolicyConfigErrorMessage = null;
        try
        {
            groupPolicyConfig = getGroupPolicy( connectorName );
        }
        catch ( Exception e )
        {
            groupPolicyConfigErrorMessage = e.getMessage();
            groupPolicyConfig = GroupPolicyConfig.ALL_FALSE;
        }

        final String pluginType = getPluginType( connectorName );
        final Boolean resurrectDeletedUsers = getResurrectDeletedUsers( connectorName );
        final Boolean resurrectDeletedGroups = getResurrectDeletedGroups( connectorName );

        final UserStoreConnectorConfig config =
            new UserStoreConnectorConfig( connectorName, pluginType, userPolicyConfig, groupPolicyConfig, resurrectDeletedUsers,
                                          resurrectDeletedGroups );

        config.addProperties( getPluginProperties( connectorName ) );

        if ( userPolicyConfigErrorMessage != null )
        {
            config.addErrorMessage( userPolicyConfigErrorMessage );
        }
        if ( groupPolicyConfigErrorMessage != null )
        {
            config.addErrorMessage( groupPolicyConfigErrorMessage );
        }

        return config;
    }

    private Set<String> doGetAllUserStoreConnectorNames()
    {
        if ( userStoreConnectorNames != null )
        {
            return userStoreConnectorNames;
        }
        userStoreConnectorNames = new HashSet<String>();

        final Properties allProperties = getSubSet( "cms.userstore.connector." );
        for ( final Object propertyName : allProperties.keySet() )
        {
            final String configName = ( (String) propertyName ).replaceAll( "^(cms\\.userstore\\.)?(.*?)\\..*$", "$2" );
            userStoreConnectorNames.add( configName );
        }
        return userStoreConnectorNames;
    }

    private String getPluginType( final String connectorName )
    {
        return getProperty( String.format( "cms.userstore.connector.%s.plugin", connectorName ) );
    }

    private Boolean getResurrectDeletedUsers( final String connectorName )
    {
        String stringValue = getProperty( String.format( "cms.userstore.connector.%s.resurrectDeletedUsers", connectorName ) );
        if ( StringUtils.isBlank( stringValue ) )
        {
            return null;
        }
        return Boolean.valueOf( stringValue );
    }

    private Boolean getResurrectDeletedGroups( final String connectorName )
    {
        String stringValue = getProperty( String.format( "cms.userstore.connector.%s.resurrectDeletedGroups", connectorName ) );
        if ( StringUtils.isBlank( stringValue ) )
        {
            return null;
        }
        return Boolean.valueOf( stringValue );
    }

    private Properties getPluginProperties( final String connectorName )
    {
        return getSubSet( String.format( "cms.userstore.connector.%s.plugin.", connectorName ) );
    }

    private UserPolicyConfig getUserPolicy( final String connectorName )
        throws InvalidUserStoreConnectorConfigException
    {
        return new UserPolicyConfig( connectorName, getProperty(
            String.format( "cms.userstore.connector.%s.userPolicy", connectorName ) ) );
    }

    private GroupPolicyConfig getGroupPolicy( final String connectorName )
    {
        return new GroupPolicyConfig( connectorName, getProperty(
            String.format( "cms.userstore.connector.%s.groupPolicy", connectorName ) ) );
    }

    private String getProperty( final String key )
    {
        final String systemProperty = StringUtils.trimToNull( System.getProperty( key ) );
        if ( systemProperty != null )
        {
            return systemProperty;
        }
        return StringUtils.trimToNull( properties.getProperty( key ) );
    }

    private Properties getSubSet( final String base )
    {
        return PropertiesUtil.getSubSet( properties, base );
    }

    @Autowired
    public void setProperties( ConfigProperties properties )
    {
        this.properties = properties;
    }
}
