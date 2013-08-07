/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.plugin.ext.userstore;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class UserStoreConfig
{
    private final Collection<UserStoreConfigField> userFieldConfigs = new TreeSet<UserStoreConfigField>();

    public Collection<UserStoreConfigField> getUserFieldConfigs()
    {
        return userFieldConfigs;
    }

    public void addUserFieldConfig( UserStoreConfigField value )
    {
        userFieldConfigs.add( value );
    }

    public void setUserFieldConfigs( final Collection<UserStoreConfigField> value )
    {
        userFieldConfigs.clear();
        userFieldConfigs.addAll( value );
    }

    public Set<UserStoreConfigField> getRemoteOnlyUserFieldConfigs()
    {
        return getUserFieldConfigs( true );
    }

    public Set<UserStoreConfigField> getLocalOnlyUserFieldConfigs()
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

    public UserStoreConfigField getUserFieldConfig( UserFieldType type )
    {
        for ( UserStoreConfigField config : userFieldConfigs )
        {
            if ( config.getType().equals( type ) )
            {
                return config;
            }
        }
        return null;
    }

    private Set<UserStoreConfigField> getUserFieldConfigs( final Boolean remoteFlagValue )
    {
        final Set<UserStoreConfigField> fieldConfigs = new LinkedHashSet<UserStoreConfigField>();

        for ( final UserStoreConfigField userFieldConfig : userFieldConfigs )
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

        for ( final UserStoreConfigField userFieldConfig : getUserFieldConfigs( remotesOnly ) )
        {
            fieldTypes.add( userFieldConfig.getType() );
        }

        return fieldTypes;
    }
}
