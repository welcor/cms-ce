/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;

public final class UserFields
    implements Iterable<UserField>
{
    private final boolean mutlipleAddresses;

    private final Multimap<UserFieldType, UserField> fields;

    public UserFields( boolean mutlipleAddresses )
    {
        this.mutlipleAddresses = mutlipleAddresses;
        this.fields = LinkedHashMultimap.create();
    }

    public UserFields()
    {
        this.mutlipleAddresses = true;
        this.fields = LinkedHashMultimap.create();
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

    public boolean hasField( final UserFieldType type )
    {
        return this.fields.containsKey( type );
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

    public Iterator<UserField> iterator()
    {
        return this.fields.values().iterator();
    }

    public void addAll( Collection<UserField> fields )
    {
        for ( UserField field : fields )
        {
            add( field );
        }
    }

    public void addAll( UserFields fields )
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

    /**
     * Removes all remote fields and adds given remote fields.
     */
    public void replaceAllRemoteFieldsOnly( UserFields fields, UserStoreConfig config )
    {
        remove( config.getRemoteOnlyUserFieldTypes() );
        addAll( fields.getRemoteFields( config ) );
    }

    public void retain( Collection<UserFieldType> types )
    {
        Set<UserFieldType> set = new HashSet<UserFieldType>( this.fields.keySet() );
        set.removeAll( types );
        remove( set );
    }

    /**
     * @return An new instance that only contains configured user fields.
     */
    public UserFields getConfiguredFieldsOnly( final UserStoreConfig userStoreConfig )
    {
        return getConfiguredFieldsOnly( userStoreConfig, false );
    }

    public UserFields getConfiguredFieldsOnly( final UserStoreConfig userStoreConfig, boolean includeMissing )
    {
        final UserFields configuredFieldsOnly = clone();
        configuredFieldsOnly.retain( userStoreConfig.getUserFieldTypes() );

        if ( includeMissing )
        {
            for ( UserFieldType type : userStoreConfig.getUserFieldTypes() )
            {
                if ( !configuredFieldsOnly.hasField( type ) )
                {
                    configuredFieldsOnly.add( new UserField( type, null ) );
                }
            }
        }

        // Set unused address part to null
        if ( configuredFieldsOnly.hasField( UserFieldType.ADDRESS ) )
        {
            final List<Address> addresses = configuredFieldsOnly.getAddresses();
            for ( Address address : addresses )
            {
                if ( userStoreConfig.getUserFieldConfig( UserFieldType.ADDRESS ).useIso() )
                {
                    address.setCountry( null );
                    address.setRegion( null );
                }
                else
                {
                    address.setIsoCountry( null );
                    address.setIsoRegion( null );
                }

            }
        }

        return configuredFieldsOnly;
    }

    public UserFields getRemoteFields( final UserStoreConfig userStoreConfig )
    {
        UserFields remoteFields = clone();
        remoteFields.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );
        return remoteFields;
    }

    public UserFields getChangedUserFields( final UserFields otherUserFields, final boolean includeMissing )
    {
        final UserFields changedUserFields = new UserFields( true );

        for ( UserField currentUserField : getAll() )
        {
            UserField otherField = otherUserFields.getField( currentUserField.getType() );
            if ( currentUserField.getValue() == null && otherField == null )
            {
                continue;
            }
            if ( !currentUserField.equals( otherField ) )
            {
                changedUserFields.add( currentUserField );
            }
        }
        if ( includeMissing )
        {
            for ( UserField otherField : otherUserFields )
            {
                UserField matchingUserField = getField( otherField.getType() );
                if ( matchingUserField == null )
                {
                    changedUserFields.add( new UserField( otherField.getType(), null ) );
                }
            }
        }
        return changedUserFields;
    }

    public void removeReadOnlyFields( final UserStoreConfig config )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : config.getUserFieldConfigs() )
        {
            if ( userFieldConfig.isReadOnly() && hasField( userFieldConfig.getType() ) )
            {
                remove( userFieldConfig.getType() );
            }
        }
    }

    public UserFields emptiesToNull()
    {
        final UserFields newUserFields = clone();
        for ( UserField field : newUserFields.getAll() )
        {
            if ( field.getValue() != null && field.getType().isStringBased() )
            {
                if ( field.getValueAsString().equals( "" ) )
                {
                    field.setValue( null );
                }
            }
        }
        return newUserFields;
    }

    public boolean existingFieldsEquals( UserFields otherFields )
    {
        for ( UserField currentField : this )
        {
            final UserField otherField = otherFields.getField( currentField.getType() );
            if ( !currentField.equals( otherField ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        UserFields that = (UserFields) o;

        if ( !fields.equals( that.fields ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return fields.hashCode();
    }

    public UserFields clone()
    {
        UserFields newMap = new UserFields( this.mutlipleAddresses );
        newMap.addAll( this.getAll() );
        return newMap;
    }

    public UserFields setAddresses( Address... addresses )
    {
        for ( Address address : addresses )
        {
            add( new UserField( UserFieldType.ADDRESS, address ) );
        }
        return this;
    }

    public UserFields setBirthday( Date value )
    {
        add( new UserField( UserFieldType.BIRTHDAY, value ) );
        return this;
    }

    public UserFields setCountry( String value )
    {
        add( new UserField( UserFieldType.COUNTRY, value ) );
        return this;
    }

    public UserFields setDescription( String value )
    {
        add( new UserField( UserFieldType.DESCRIPTION, value ) );
        return this;
    }

    public UserFields setFax( String value )
    {
        add( new UserField( UserFieldType.FAX, value ) );
        return this;
    }

    public UserFields setFirstName( String value )
    {
        add( new UserField( UserFieldType.FIRST_NAME, value ) );
        return this;
    }

    public UserFields setGender( Gender value )
    {
        add( new UserField( UserFieldType.GENDER, value ) );
        return this;
    }

    public UserFields setGlobalPosition( String value )
    {
        add( new UserField( UserFieldType.GLOBAL_POSITION, value ) );
        return this;
    }

    public UserFields setHomePage( String value )
    {
        add( new UserField( UserFieldType.HOME_PAGE, value ) );
        return this;
    }

    public UserFields setHtmlEmail( Boolean value )
    {
        add( new UserField( UserFieldType.HTML_EMAIL, value ) );
        return this;
    }

    public UserFields setInitials( String value )
    {
        add( new UserField( UserFieldType.INITIALS, value ) );
        return this;
    }

    public UserFields setLastName( String value )
    {
        add( new UserField( UserFieldType.LAST_NAME, value ) );
        return this;
    }

    public UserFields setLocale( Locale value )
    {
        add( new UserField( UserFieldType.LOCALE, value ) );
        return this;
    }

    public UserFields setMemberId( String value )
    {
        add( new UserField( UserFieldType.MEMBER_ID, value ) );
        return this;
    }

    public UserFields setMiddleName( String value )
    {
        add( new UserField( UserFieldType.MIDDLE_NAME, value ) );
        return this;
    }

    public UserFields setMobile( String value )
    {
        add( new UserField( UserFieldType.MOBILE, value ) );
        return this;
    }

    public UserFields setNickName( String value )
    {
        add( new UserField( UserFieldType.NICK_NAME, value ) );
        return this;
    }

    public UserFields setOrganization( String value )
    {
        add( new UserField( UserFieldType.ORGANIZATION, value ) );
        return this;
    }

    public UserFields setPersonalId( String value )
    {
        add( new UserField( UserFieldType.PERSONAL_ID, value ) );
        return this;
    }

    public UserFields setPhone( String value )
    {
        add( new UserField( UserFieldType.PHONE, value ) );
        return this;
    }

    public UserFields setPhoto( byte[] value )
    {
        add( new UserField( UserFieldType.PHOTO, value ) );
        return this;
    }

    public UserFields setPrefix( String value )
    {
        add( new UserField( UserFieldType.PREFIX, value ) );
        return this;
    }

    public UserFields setSuffix( String value )
    {
        add( new UserField( UserFieldType.SUFFIX, value ) );
        return this;
    }

    public UserFields setTimezone( TimeZone value )
    {
        add( new UserField( UserFieldType.TIME_ZONE, value ) );
        return this;
    }

    public UserFields setTitle( String value )
    {
        add( new UserField( UserFieldType.TITLE, value ) );
        return this;
    }

    public String getPrefix()
    {
        return getUserFieldValueAsString( UserFieldType.PREFIX );
    }

    public String getFirstName()
    {
        return getUserFieldValueAsString( UserFieldType.FIRST_NAME );
    }

    public String getMiddleName()
    {
        return getUserFieldValueAsString( UserFieldType.MIDDLE_NAME );
    }

    public String getLastName()
    {
        return getUserFieldValueAsString( UserFieldType.LAST_NAME );
    }

    public String getSuffix()
    {
        return getUserFieldValueAsString( UserFieldType.SUFFIX );
    }

    public String getNickName()
    {
        return getUserFieldValueAsString( UserFieldType.NICK_NAME );
    }

    public String getInitials()
    {
        return getUserFieldValueAsString( UserFieldType.INITIALS );
    }

    public String getCountry()
    {
        return getUserFieldValueAsString( UserFieldType.COUNTRY );
    }

    public Date getBirthday()
    {
        return getUserFieldValueAsDate( UserFieldType.BIRTHDAY );
    }

    public String getHomePage()
    {
        return getUserFieldValueAsString( UserFieldType.HOME_PAGE );
    }

    public String getOrganization()
    {
        return getUserFieldValueAsString( UserFieldType.ORGANIZATION );
    }

    public String getPhone()
    {
        return getUserFieldValueAsString( UserFieldType.PHONE );
    }

    public Locale getLocale()
    {
        return getUserFieldValueAsLocale( UserFieldType.LOCALE );
    }

    public Boolean getHtmlEmail()
    {
        return getUserFieldValueAsBoolean( UserFieldType.HTML_EMAIL );
    }

    public Gender getGender()
    {
        return getUserFieldValueAsGender( UserFieldType.GENDER );
    }

    public byte[] getPhoto()
    {
        return getUserFieldValueAsBytes( UserFieldType.PHOTO );
    }

    public List<Address> getAddresses()
    {
        return getUserFieldValueAsAddresses( UserFieldType.ADDRESS );
    }

    public TimeZone getTimeZone()
    {
        return getUserFieldValueAsTimeZone( UserFieldType.TIME_ZONE );
    }

    public String getTitle()
    {
        return getUserFieldValueAsString( UserFieldType.TITLE );
    }

    public String getDescription()
    {
        return getUserFieldValueAsString( UserFieldType.DESCRIPTION );
    }

    private String getUserFieldValueAsString( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsString();
    }

    private Date getUserFieldValueAsDate( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsDate();
    }

    private Locale getUserFieldValueAsLocale( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsLocale();
    }

    private Boolean getUserFieldValueAsBoolean( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsBoolean();
    }

    private Gender getUserFieldValueAsGender( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsGender();
    }

    private TimeZone getUserFieldValueAsTimeZone( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsTimeZone();
    }

    private byte[] getUserFieldValueAsBytes( UserFieldType type )
    {
        final UserField userField = getField( type );
        if ( userField == null )
        {
            return null;
        }
        return userField.getValueAsBytes();
    }


    private List<Address> getUserFieldValueAsAddresses( UserFieldType type )
    {
        final List<Address> addresses = Lists.newArrayList();
        for ( UserField userField : getFields( type ) )
        {
            addresses.add( userField.getValueAsAddress() );
        }
        return addresses;
    }
}
