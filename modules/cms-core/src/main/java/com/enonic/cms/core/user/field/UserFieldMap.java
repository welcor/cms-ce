/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;

public final class UserFieldMap
    implements Iterable<UserField>, Comparable<UserFieldMap>
{
    private final boolean mutlipleAddresses;

    private final Multimap<UserFieldType, UserField> fields;

    public UserFieldMap( boolean mutlipleAddresses )
    {
        this.mutlipleAddresses = mutlipleAddresses;
        this.fields = LinkedHashMultimap.create();
    }

    public UserField getField( UserFieldType type )
    {
        Collection<UserField> result = this.fields.get( type );
        if ( ( result != null ) && !result.isEmpty() )
        {
            return result.iterator().next();
        }
        else
        {
            return null;
        }
    }

    public Collection<UserField> getFields( UserFieldType type )
    {
        return this.fields.get( type );
    }

    public boolean hasField( final UserFieldType type )
    {
        return this.fields.containsKey( type );
    }

    public Iterator<UserField> iterator()
    {
        return this.fields.values().iterator();
    }

    public void add( final UserField field )
    {
        final UserFieldType type = field.getType();
        if ( type == UserFieldType.ADDRESS )
        {
            if ( this.mutlipleAddresses || !this.fields.containsKey( type ) )
            {
                this.fields.put( type, field );
            }
        }
        else
        {
            this.fields.removeAll( type );
            this.fields.put( type, field );
        }
    }

    public void addAll( Collection<UserField> fields )
    {
        for ( UserField field : fields )
        {
            add( field );
        }
    }

    public Collection<UserField> getAll()
    {
        return this.fields.values();
    }

    public void clear()
    {
        this.fields.clear();
    }

    public int getSize()
    {
        return this.fields.size();
    }

    public void remove( UserFieldType type )
    {
        this.fields.removeAll( type );
    }

    public void remove( Collection<UserFieldType> types )
    {
        for ( UserFieldType type : types )
        {
            this.fields.removeAll( type );
        }
    }

    public void retain( Collection<UserFieldType> types )
    {
        Set<UserFieldType> set = new HashSet<UserFieldType>( this.fields.keySet() );
        set.removeAll( types );
        remove( set );
    }

    public UserFieldMap getRemoteFields( UserStoreConfig userStoreConfig )
    {
        UserFieldMap remoteFields = clone();
        remoteFields.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );
        return remoteFields;
    }

    public UserFieldMap clone()
    {
        UserFieldMap newMap = new UserFieldMap( this.mutlipleAddresses );
        newMap.addAll( this.getAll() );
        return newMap;
    }

    /**
     * Compares this userFieldMap with another.
     *
     * @param remoteUserFields The map of fields to compare to.
     * @param twoWayCompare    Determines if it is necessary for this map to contain all the contents of the <code>remoteUserFields</code> map.
     *                         If this parameter is set to <code>false</code>, <code>true</code> will be returned, as long as all the values of this map exist
     *                         in the <code>remoteUserFields</code>.  If this parameter is set to <code>true</code>, all the values in both maps
     *                         need to be exactly the same.
     * @return 2 if both maps contain a value for a field, but they are not equal
     *         1 if this map contain a value that does not exist in the remote map.
     *         0 if both maps are equal.  If <code>twoWayCompare</code> is true, both maps are exactly the same, otherwise, a 0 indicates
     *         that all fields in this map are contained with the same values in the remote map, but the remote map may contain other values.
     *         -1 if the remote map contains values that are not in this map.  This can only happen if <code>twoWayCompare</code> is
     *         <code>true</code>.
     */
    public int compareTo( UserFieldMap remoteUserFields, boolean twoWayCompare )
    {
        for ( UserField commandField : this )
        {
            UserField remoteField = remoteUserFields.getField( commandField.getType() );
            int diff = commandField.compareTo( remoteField );
            if ( diff != 0 )
            {
                return diff;
            }
        }
        if ( twoWayCompare )
        {
            for ( UserField remoteField : remoteUserFields )
            {
                UserField commandFieldMatchingRemote = getField( remoteField.getType() );
                if ( commandFieldMatchingRemote == null )
                {
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * Shorthand for <code>compareTo(userFields, true)</code>.
     *
     * @param userFields A map of the fields to compare to.
     * @return 2 if both maps contain a value for a field, but they are not equal
     *         1 if this map contain a value that does not exist in the remote map.
     *         0 if both maps are exactly equal.
     *         -1 if the remote map contains values that are not in this map.
     */
    public int compareTo( UserFieldMap userFields )
    {
        return compareTo( userFields, true );
    }}
