/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldTransformer;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;

public class UserEntity
    implements User, Serializable
{
    private UserKey key;

    private String name;

    private String displayName;

    private Integer deleted;

    private UserType type;

    private DateTime timestamp;

    private String syncValue;

    private String email;

    private UserStoreEntity userStore;

    private GroupEntity userGroup;

    private byte[] photo;

    private String password;

    private Map<String, String> fieldMap = new HashMap<String, String>();

    private transient UserFields userFields = null;

    private transient QualifiedUsername qualifiedName;

    private transient List<GroupKey> allMembershipsGroupKeys;

    public UserEntity()
    {
        // Default constructor used by Hibernate.
    }

    public UserEntity( UserEntity source )
    {
        this();

        this.key = source.getKey();
        this.name = source.getName();
        this.displayName = source.getDisplayName();
        this.email = source.getEmail();
        this.deleted = source.getDeleted();
        this.type = source.getType();
        this.timestamp = source.getTimestamp();
        this.syncValue = source.getSync();
        this.email = source.getEmail();
        this.userStore = source.getUserStore() != null ? new UserStoreEntity( source.getUserStore() ) : null;
        this.userGroup = source.getUserGroup();
        this.photo = source.getPhoto();
        this.password = source.getPassword();
        this.fieldMap = source.getFieldMap() != null ? Maps.newHashMap( source.getFieldMap() ) : null;
    }

    public UserKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getEmail()
    {
        return this.email;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public boolean isDeleted()
    {
        return deleted != 0;
    }

    public UserType getType()
    {
        return type;
    }

    public boolean isAnonymous()
    {
        return getType().isAnonymous();
    }

    /**
     * @return true if user is the hard coded admin super user.
     */
    public boolean isRoot()
    {
        return getName().equals( ROOT_UID );
    }

    public boolean isEnterpriseAdmin()
    {
        if ( isRoot() )
        {
            return true;
        }
        if ( isAnonymous() )
        {
            return false;
        }

        if ( getUserGroup() == null )
        {

            return false;
        }

        return getUserGroup().isOfType( GroupType.ENTERPRISE_ADMINS, true );
    }

    public static boolean isBuiltInUser( String uid )
    {
        return uid.equalsIgnoreCase( ROOT_UID ) || uid.equalsIgnoreCase( ANONYMOUS_UID );
    }

    public boolean isBuiltIn()
    {
        if ( getType().isBuiltIn() )
        {
            return true;
        }

        return getUserGroup() != null && getUserGroup().isBuiltIn();
    }

    public DateTime getTimestamp()
    {
        return timestamp;
    }

    public String getSync()
    {
        return syncValue;
    }

    public UserStoreEntity getUserStore()
    {
        return userStore;
    }

    public UserStoreKey getUserStoreKey()
    {
        return getUserStore() != null ? getUserStore().getKey() : null;
    }

    public void setKey( UserKey key )
    {
        this.key = key;
    }

    public void setName( String value )
    {
        this.name = value;

        // invalidate
        qualifiedName = null;
    }

    public void setDisplayName( final String value )
    {
        displayName = value;
    }

    public void setDeleted( int deleted )
    {
        this.deleted = deleted;
    }

    public void setDeleted( boolean value )
    {
        this.deleted = value ? 1 : 0;
    }

    public void setType( UserType value )
    {
        type = value;
    }

    public void setTimestamp( DateTime value )
    {
        this.timestamp = value;
    }

    public void setSyncValue( String value )
    {
        this.syncValue = value;
    }

    public void setEmail( String value )
    {
        this.email = value;
    }

    public void setUserStore( UserStoreEntity value )
    {
        this.userStore = value;

        // invalidate
        qualifiedName = null;
    }

    public void setUserGroup( GroupEntity value )
    {
        this.userGroup = value;
    }

    public GroupKey getUserGroupKey()
    {
        return getUserGroup() != null ? getUserGroup().getGroupKey() : null;
    }

    public GroupEntity getUserGroup()
    {
        return userGroup;
    }

    public QualifiedUsername getQualifiedName()
    {

        if ( qualifiedName == null )
        {
            String uid = getName();
            if ( "anonymous".equals( uid ) || "admin".equals( uid ) )
            {
                qualifiedName = new QualifiedUsername( uid );
            }
            else if ( userStore != null )
            {
                qualifiedName = new QualifiedUsername( userStore.getName(), uid );
            }
        }

        return qualifiedName;
    }

    /**
     * @return The distinct set of all group keys (recursively) of this users memberships, including the user group key.
     */
    public List<GroupKey> getAllMembershipsGroupKeys()
    {

        if ( allMembershipsGroupKeys == null )
        {
            allMembershipsGroupKeys = new ArrayList<GroupKey>();
            GroupEntity userGroup = getUserGroup();
            allMembershipsGroupKeys.add( userGroup.getGroupKey() );
            allMembershipsGroupKeys.addAll( userGroup.getAllMembershipsGroupKeys() );
        }

        return allMembershipsGroupKeys;
    }

    /**
     * @return The distinct set of all groups, including recursively fetched subgroups of this users memberships.
     */
    public Set<GroupEntity> getAllMembershipsGroups()
    {

        if ( getUserGroup() != null )
        {
            return getUserGroup().getAllMemberships();
        }
        else
        {
            return new HashSet<GroupEntity>();
        }
    }

    public boolean isMemberOf( GroupEntity group, boolean recursively )
    {
        final GroupEntity userGroup = getUserGroup();
        if ( userGroup == null )
        {
            return false;
        }
        return group.equals( userGroup ) || userGroup.isMemberOf( group, recursively );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof UserEntity ) )
        {
            return false;
        }

        UserEntity that = (UserEntity) o;

        return getKey().equals( that.getKey() );

    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 273;
        final int multiplierNonZeroOddNumber = 637;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( getKey() ).toHashCode();
    }

    public String getPassword()
    {
        return this.password;
    }

    public void encodePassword( String password )
    {
        if ( password != null )
        {
            this.password = DigestUtils.shaHex( password );
        }
        else
        {
            this.password = null;
        }
    }

    public boolean verifyPassword( String password )
    {
        if ( password == null )
        {
            return this.password == null;
        }

        return DigestUtils.shaHex( password ).equals( this.password );
    }

    public String getSelectedLanguageCode()
    {
        return null;
    }

    public void setSelectedLanguageCode( String languageCode )
    {

    }

    public boolean hasUserGroup()
    {
        return getUserGroup() != null;
    }

    public Set<GroupEntity> getAllMemberships()
    {
        return getAllMembershipsGroups();
    }

    public Set<GroupEntity> getDirectMemberships()
    {
        if ( !hasUserGroup() )
        {
            return new HashSet<GroupEntity>();
        }

        return getUserGroup().getMemberships( false );
    }

    void setFieldMap( Map<String, String> fieldMap )
    {
        this.fieldMap = fieldMap;
    }

    public Map<String, String> getFieldMap()
    {
        return fieldMap;
    }

    public UserFields getUserFields()
    {
        return doGetUserFields();
    }

    private UserFields doGetUserFields()
    {
        if ( this.userFields == null )
        {
            final UserFieldTransformer fieldTransformer = new UserFieldTransformer();
            userFields = fieldTransformer.fromStoreableMap( this.fieldMap );
            fieldTransformer.updatePhoto( userFields, this.photo );
        }
        return this.userFields;
    }

    /**
     * @return true if changes where made.
     */
    public boolean overwriteUserFields( final UserFields userFields )
    {
        final Map<String, String> newFieldMap = new UserFieldTransformer().toStoreableMap( userFields );
        removeFieldsWithNullValueWhenFieldDoesNotExist( newFieldMap );

        final Map<String, String> oldFieldMap = Maps.newHashMap( this.fieldMap );

        this.fieldMap.putAll( newFieldMap );
        final UserField photoField = userFields.getField( UserFieldType.PHOTO );
        this.photo = photoField != null ? (byte[]) photoField.getValue() : null;

        // invalidate
        this.userFields = null;

        return !oldFieldMap.equals( fieldMap );
    }

    /**
     * Replaces user fiels with given and returns true if it changed from previous user fields.
     */
    public boolean setUserFields( final UserFields userFields )
    {
        final Map<String, String> newFieldMap = new UserFieldTransformer().toStoreableMap( userFields );
        removeFieldsWithNullValueWhenFieldDoesNotExist( newFieldMap );

        final Map<String, String> oldFieldMap = Maps.newHashMap( this.fieldMap );

        this.fieldMap.clear();
        fieldMap.putAll( newFieldMap );
        final UserField photoField = userFields.getField( UserFieldType.PHOTO );
        this.photo = photoField != null ? (byte[]) photoField.getValue() : null;

        // invalidate          
        this.userFields = null;

        return !oldFieldMap.equals( fieldMap );
    }

    public boolean isInRemoteUserStore()
    {
        return !isBuiltIn() && getUserStore() != null && getUserStore().isRemote();
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public boolean hasPhoto()
    {
        return photo != null;
    }

    public void setPhoto( byte[] photo )
    {
        this.photo = photo;
    }

    private void removeFieldsWithNullValueWhenFieldDoesNotExist( final Map<String, String> mapToRemoveFrom )
    {
        final List<String> fieldsToRemove = new ArrayList<String>();
        for ( Map.Entry<String, String> entryToPossibleRemoveIfValueIsNull : mapToRemoveFrom.entrySet() )
        {
            if ( entryToPossibleRemoveIfValueIsNull.getValue() == null &&
                !this.fieldMap.containsKey( entryToPossibleRemoveIfValueIsNull.getKey() ) )
            {
                fieldsToRemove.add( entryToPossibleRemoveIfValueIsNull.getKey() );
            }
        }

        for ( String fieldToRemove : fieldsToRemove )
        {
            mapToRemoveFrom.remove( fieldToRemove );
        }
    }
}
