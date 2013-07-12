/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.plugin.userstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;

public final class UserFields
    implements Iterable<UserField>
{
    private final boolean multipleAddresses;

    private final List<UserFieldType> typeList;

    private final List<UserField> userFieldList;

    public UserFields()
    {
        this( true );
    }

    public UserFields( boolean multipleAddresses )
    {
        this.multipleAddresses = multipleAddresses;

        this.typeList = new ArrayList<UserFieldType>();
        this.userFieldList = new ArrayList<UserField>();
    }

    public void add( final UserField field )
    {
        final UserFieldType type = field.getType();

        if ( type == UserFieldType.ADDRESS )
        {
            if ( this.multipleAddresses || !hasField( type ) )
            {
                internalAdd( type, field );
            }
        }
        else
        {
            remove( type );
            internalAdd( type, field );
        }
    }

    private void internalAdd( final UserFieldType type, final UserField field )
    {
        this.typeList.add( type );
        this.userFieldList.add( field );
    }

    public boolean hasField( final UserFieldType type )
    {
        return this.typeList.contains( type );
    }

    public UserField getField( UserFieldType type )
    {
        final Collection<UserField> result = getFields( type );

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
        final List<UserField> fields = new ArrayList<UserField>();

        final int size = typeList.size();

        for ( int i = 0; i < size; i++ )
        {
            if ( type.equals( typeList.get( i ) ) )
            {
                fields.add( userFieldList.get( i ) );
            }
        }

        return fields;
    }

    public Iterator<UserField> iterator()
    {
        return getAll().iterator();
    }

    public void addAll( Collection<UserField> fields )
    {
        for ( final UserField field : fields )
        {
            add( field );
        }
    }

    public void addAll( UserFields fields )
    {
        for ( final UserField field : fields )
        {
            add( field );
        }
    }

    public Collection<UserField> getAll()
    {
        return this.userFieldList;
    }

    public void clear()
    {
        this.typeList.clear();
        this.userFieldList.clear();
    }

    public int getSize()
    {
        return this.typeList.size();
    }

    public void remove( UserFieldType type )
    {
        for ( int i = typeList.size() - 1; i >= 0; i-- )
        {
            if ( type.equals( typeList.get( i ) ) )
            {
                this.typeList.remove( i );
                this.userFieldList.remove( i );
            }
        }
    }

    public void remove( Collection<UserFieldType> types )
    {
        for ( final UserFieldType type : types )
        {
            remove( type );
        }
    }

    public void retain( Collection<UserFieldType> types )
    {
        final Set<UserFieldType> set = new HashSet<UserFieldType>( this.typeList );
        set.removeAll( types );
        remove( set );
    }

    /**
     * Removes all remote fields and adds given remote fields.
     */
    public void replaceAllRemoteFieldsOnly( UserFields fields, UserStoreConfig config )
    {
        remove( config.getRemoteOnlyUserFieldTypes() );
        addAll( fields.getRemoteFields( config ) );
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

        final UserFields that = (UserFields) o;

        if ( !this.typeList.equals( that.typeList ) )
        {
            return false;
        }

        if ( !this.userFieldList.equals( that.userFieldList ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return this.typeList.hashCode() + this.userFieldList.hashCode();
    }

    public UserFields clone()
    {
        final UserFields newMap = new UserFields( this.multipleAddresses );
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
        final List<Address> addresses = new ArrayList<Address>();

        for ( final UserField userField : getFields( type ) )
        {
            addresses.add( userField.getValueAsAddress() );
        }

        return addresses;
    }
}
