/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.enonic.cms.core.user.field.UserFieldType;

public class UserStoreConfig
{
    private final Collection<UserStoreUserFieldConfig> userFieldConfigs = new TreeSet<UserStoreUserFieldConfig>();

    public Collection<UserStoreUserFieldConfig> getUserFieldConfigs()
    {
        return userFieldConfigs;
    }

    public void addUserFieldConfig( UserStoreUserFieldConfig value )
    {
        userFieldConfigs.add( value );
    }

    public void setUserFieldConfigs( final Collection<UserStoreUserFieldConfig> value )
    {
        userFieldConfigs.clear();
        userFieldConfigs.addAll( value );
    }

    public Set<UserStoreUserFieldConfig> getRemoteOnlyUserFieldConfigs()
    {
        return getUserFieldConfigs( true );
    }

    public Set<UserStoreUserFieldConfig> getLocalOnlyUserFieldConfigs()
    {
        return getUserFieldConfigs( false );
    }

    public Set<UserFieldType> getUserFieldTypes()
    {
        return getUserFieldTypes( null );
    }

    public Set<UserFieldType> getRemoteOnlyUserFieldTypes()
    {
        return getUserFieldTypes( true );
    }

    public Set<UserFieldType> getLocalOnlyUserFieldTypes()
    {
        return getUserFieldTypes( false );
    }

    public UserStoreUserFieldConfig getUserFieldConfig( UserFieldType type )
    {
        for ( UserStoreUserFieldConfig config : userFieldConfigs )
        {
            if ( config.getType().equals( type ) )
            {
                return config;
            }
        }
        return null;
    }

    private Set<UserStoreUserFieldConfig> getUserFieldConfigs( final Boolean remoteFlagValue )
    {
        final Set<UserStoreUserFieldConfig> fieldConfigs = new LinkedHashSet<UserStoreUserFieldConfig>();

        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( remoteFlagValue == null || userFieldConfig.isRemote() == remoteFlagValue )
            {
                fieldConfigs.add( userFieldConfig );
            }
        }
        return fieldConfigs;
    }

    private Set<UserFieldType> getUserFieldTypes( final Boolean remotesOnly )
    {
        final Set<UserFieldType> fieldTypes = new LinkedHashSet<UserFieldType>();

        for ( final UserStoreUserFieldConfig userFieldConfig : getUserFieldConfigs( remotesOnly ) )
        {
            fieldTypes.add( userFieldConfig.getType() );
        }

        return fieldTypes;
    }
}
