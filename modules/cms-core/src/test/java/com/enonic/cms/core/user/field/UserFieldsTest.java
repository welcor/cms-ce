/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.user.field;


import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;

import static org.junit.Assert.*;

public class UserFieldsTest
{
    @Test
    public void getRemoteFields_given_no_fields_then_none_is_returned()
    {
        // setup
        UserFields userFields = new UserFields( false );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, false ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, false ) );

        // exercise
        UserFields remoteUserFields = userFields.getRemoteFields( userStoreConfig );

        // verify
        assertEquals( 0, remoteUserFields.getSize() );
    }

    @Test
    public void getRemoteFields_given_fields_but_none_is_remote_then_none_is_returned()
    {
        // setup
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        userFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, false ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, false ) );

        // exercise
        UserFields remoteUserFields = userFields.getRemoteFields( userStoreConfig );

        // verify
        assertEquals( 0, remoteUserFields.getSize() );
    }

    @Test
    public void getRemoteFields_given_fields_and_one_is_remote_then_one_is_returned()
    {
        // setup
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        userFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );
        userFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, false ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, false ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, true ) );

        // exercise
        UserFields remoteUserFields = userFields.getRemoteFields( userStoreConfig );

        // verify
        assertEquals( 1, remoteUserFields.getSize() );
        assertEquals( "Lastname", remoteUserFields.getField( UserFieldType.LAST_NAME ).getValue() );
    }

    @Test
    public void getRemoteFields_given_fields_and_one_is_local_then_all_but_one_is_returned()
    {
        // setup
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        userFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );
        userFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        userFields.add( new UserField( UserFieldType.PHONE, "12345678" ) );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, true ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, false ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, true ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.PHONE, true ) );

        // exercise
        UserFields remoteUserFields = userFields.getRemoteFields( userStoreConfig );

        // verify
        assertEquals( 3, remoteUserFields.getSize() );
        assertEquals( "FL", remoteUserFields.getField( UserFieldType.INITIALS ).getValue() );
        assertEquals( "12345678", remoteUserFields.getField( UserFieldType.PHONE ).getValue() );
        assertEquals( "Firstname", remoteUserFields.getField( UserFieldType.FIRST_NAME ).getValue() );
    }

    @Test
    public void getRemoteFields_given_fields_and_all_is_remote_then_all_is_returned()
    {
        // setup
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        userFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );
        userFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        userFields.add( new UserField( UserFieldType.PHONE, "12345678" ) );
        Address address = new Address();
        address.setStreet( "My street 1" );
        userFields.add( new UserField( UserFieldType.ADDRESS, address ) );

        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, true ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, true ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, true ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.PHONE, true ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ADDRESS, true ) );

        // exercise
        UserFields remoteUserFields = userFields.getRemoteFields( userStoreConfig );

        // verify
        assertEquals( 5, remoteUserFields.getSize() );
        assertEquals( "FL", remoteUserFields.getField( UserFieldType.INITIALS ).getValue() );
        assertEquals( "12345678", remoteUserFields.getField( UserFieldType.PHONE ).getValue() );
        assertEquals( "Firstname", remoteUserFields.getField( UserFieldType.FIRST_NAME ).getValue() );
        assertEquals( "Lastname", remoteUserFields.getField( UserFieldType.LAST_NAME ).getValue() );
        assertEquals( "My street 1", ( (Address) remoteUserFields.getField( UserFieldType.ADDRESS ).getValue() ).getStreet() );
    }

    @Test
    public void existingFieldsEquals_given_empty_when_this_is_empty_then_true_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( true );
        UserFields remoteUserFields = new UserFields( true );

        // exercise & verify
        assertEquals( true, localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_same_fields_with_equal_values_then_true_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( true );
        Address localAddress = new Address();
        localAddress.setLabel( "Label" );
        localAddress.setStreet( "Street" );
        localAddress.setPostalCode( "0001" );
        localAddress.setPostalAddress( "Oslo" );
        localAddress.setRegion( "Oslo" );
        localAddress.setCountry( "Norway" );
        localUserFields.add( new UserField( UserFieldType.ADDRESS, localAddress ) );
        localUserFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );
        localUserFields.add( new UserField( UserFieldType.COUNTRY, "Country" ) );
        localUserFields.add( new UserField( UserFieldType.DESCRIPTION, "Description" ) );
        localUserFields.add( new UserField( UserFieldType.FAX, "01010101" ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.GENDER, Gender.FEMALE ) );
        localUserFields.add( new UserField( UserFieldType.GLOBAL_POSITION, "1234ABCD" ) );
        localUserFields.add( new UserField( UserFieldType.HOME_PAGE, "http://www.enonic.com" ) );
        localUserFields.add( new UserField( UserFieldType.HTML_EMAIL, Boolean.TRUE ) );
        localUserFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );
        localUserFields.add( new UserField( UserFieldType.LOCALE, Locale.GERMANY ) );
        localUserFields.add( new UserField( UserFieldType.MEMBER_ID, "MemberId123" ) );
        localUserFields.add( new UserField( UserFieldType.MIDDLE_NAME, "Middle name" ) );
        localUserFields.add( new UserField( UserFieldType.MOBILE, "98765432" ) );
        localUserFields.add( new UserField( UserFieldType.NICK_NAME, "Nick" ) );
        localUserFields.add( new UserField( UserFieldType.ORGANIZATION, "Organization" ) );
        localUserFields.add( new UserField( UserFieldType.PERSONAL_ID, "PersonalId123" ) );
        localUserFields.add( new UserField( UserFieldType.PHONE, "12345678" ) );
        localUserFields.add( new UserField( UserFieldType.PHOTO, new byte[]{123} ) );
        localUserFields.add( new UserField( UserFieldType.PREFIX, "Prefix" ) );
        localUserFields.add( new UserField( UserFieldType.SUFFIX, "Suffix" ) );
        localUserFields.add( new UserField( UserFieldType.TIME_ZONE, TimeZone.getTimeZone( "UTC" ) ) );
        localUserFields.add( new UserField( UserFieldType.TITLE, "Title" ) );

        UserFields remoteUserFields = new UserFields( true );
        Address remoteAddress = new Address();
        remoteAddress.setLabel( "Label" );
        remoteAddress.setStreet( "Street" );
        remoteAddress.setPostalCode( "0001" );
        remoteAddress.setPostalAddress( "Oslo" );
        remoteAddress.setRegion( "Oslo" );
        remoteAddress.setCountry( "Norway" );
        remoteUserFields.add( new UserField( UserFieldType.ADDRESS, remoteAddress ) );
        remoteUserFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );
        remoteUserFields.add( new UserField( UserFieldType.COUNTRY, "Country" ) );
        remoteUserFields.add( new UserField( UserFieldType.DESCRIPTION, "Description" ) );
        remoteUserFields.add( new UserField( UserFieldType.FAX, "01010101" ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.GENDER, Gender.FEMALE ) );
        remoteUserFields.add( new UserField( UserFieldType.GLOBAL_POSITION, "1234ABCD" ) );
        remoteUserFields.add( new UserField( UserFieldType.HOME_PAGE, "http://www.enonic.com" ) );
        remoteUserFields.add( new UserField( UserFieldType.HTML_EMAIL, Boolean.TRUE ) );
        remoteUserFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LOCALE, Locale.GERMANY ) );
        remoteUserFields.add( new UserField( UserFieldType.MEMBER_ID, "MemberId123" ) );
        remoteUserFields.add( new UserField( UserFieldType.MIDDLE_NAME, "Middle name" ) );
        remoteUserFields.add( new UserField( UserFieldType.MOBILE, "98765432" ) );
        remoteUserFields.add( new UserField( UserFieldType.NICK_NAME, "Nick" ) );
        remoteUserFields.add( new UserField( UserFieldType.ORGANIZATION, "Organization" ) );
        remoteUserFields.add( new UserField( UserFieldType.PERSONAL_ID, "PersonalId123" ) );
        remoteUserFields.add( new UserField( UserFieldType.PHONE, "12345678" ) );
        remoteUserFields.add( new UserField( UserFieldType.PHOTO, new byte[]{123} ) );
        remoteUserFields.add( new UserField( UserFieldType.PREFIX, "Prefix" ) );
        remoteUserFields.add( new UserField( UserFieldType.SUFFIX, "Suffix" ) );
        remoteUserFields.add( new UserField( UserFieldType.TIME_ZONE, TimeZone.getTimeZone( "UTC" ) ) );
        remoteUserFields.add( new UserField( UserFieldType.TITLE, "Title" ) );

        // exercise & verify
        assertEquals( true, localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_photo_field_then_false()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.PHOTO, new byte[]{123} ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.PHOTO, new byte[]{101} ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_gender_field_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.GENDER, Gender.MALE ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.GENDER, Gender.FEMALE ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_htmlEmail_field_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.HTML_EMAIL, Boolean.FALSE ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.HTML_EMAIL, Boolean.TRUE ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_locale_field_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.LOCALE, Locale.GERMANY ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.LOCALE, Locale.FRENCH ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_timeZone_field_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.TIME_ZONE, TimeZone.getTimeZone( "UTC" ) ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.TIME_ZONE, TimeZone.getTimeZone( "HST" ) ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_birthDay_field_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 1, 19 ).toDate() ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_adress_field_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( true );
        Address localAddress = new Address();
        localAddress.setLabel( "Label" );
        localAddress.setStreet( "Street" );
        localAddress.setPostalCode( "0001" );
        localAddress.setPostalAddress( "Oslo" );
        localAddress.setRegion( "Oslo" );
        localAddress.setCountry( "Norway" );
        localUserFields.add( new UserField( UserFieldType.ADDRESS, localAddress ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( true );
        Address remoteAddress = new Address();
        remoteAddress.setLabel( "Label" );
        remoteAddress.setStreet( "Street" );
        remoteAddress.setPostalCode( "0002" );
        remoteAddress.setPostalAddress( "Oslo" );
        remoteAddress.setRegion( "Oslo" );
        remoteAddress.setCountry( "Norway" );
        remoteUserFields.add( new UserField( UserFieldType.ADDRESS, remoteAddress ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_unequal_number_of_adresses_fields_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( true );
        Address localAddress1 = new Address();
        localAddress1.setLabel( "Label" );
        localAddress1.setStreet( "Street" );
        localAddress1.setPostalCode( "0001" );
        localAddress1.setPostalAddress( "Oslo" );
        localAddress1.setRegion( "Oslo" );
        localAddress1.setCountry( "Norway" );
        localUserFields.add( new UserField( UserFieldType.ADDRESS, localAddress1 ) );
        Address localAddress2 = new Address();
        localAddress2.setLabel( "Label2" );
        localAddress2.setStreet( "Street 2" );
        localAddress2.setPostalCode( "0002" );
        localAddress2.setPostalAddress( "Oslo" );
        localAddress2.setRegion( "Oslo" );
        localAddress2.setCountry( "Norway" );
        localUserFields.add( new UserField( UserFieldType.ADDRESS, localAddress2 ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( true );
        Address remoteAddress = new Address();
        remoteAddress.setLabel( "Label" );
        remoteAddress.setStreet( "Street" );
        remoteAddress.setPostalCode( "0001" );
        remoteAddress.setPostalAddress( "Oslo" );
        remoteAddress.setRegion( "Oslo" );
        remoteAddress.setCountry( "Norway" );
        remoteUserFields.add( new UserField( UserFieldType.ADDRESS, remoteAddress ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_equal_fields_but_less_fields_in_this_then_true_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        remoteUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        // exercise & verify
        assertTrue( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void existingFieldsEquals_given_equal_fields_but_less_fields_in_other_then_false_is_returned()
    {
        // setup
        UserFields localUserFields = new UserFields( false );
        localUserFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        localUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );
        localUserFields.add( new UserField( UserFieldType.LAST_NAME, "Lastname" ) );

        UserFields remoteUserFields = new UserFields( false );
        remoteUserFields.add( new UserField( UserFieldType.INITIALS, "FL" ) );
        remoteUserFields.add( new UserField( UserFieldType.FIRST_NAME, "Firstname" ) );

        // exercise & verify
        assertFalse( localUserFields.existingFieldsEquals( remoteUserFields ) );
    }

    @Test
    public void getConfiguredFieldsOnly()
    {
        // setup
        UserFields allUserFields = new UserFields( false );
        allUserFields.add( new UserField( UserFieldType.FIRST_NAME, "First name" ) );
        allUserFields.add( new UserField( UserFieldType.LAST_NAME, "Last name" ) );
        allUserFields.add( new UserField( UserFieldType.PHONE, "Phone" ) );
        Address address = new Address();
        address.setLabel( "Label" );
        address.setIsoCountry( "IsoCountry" );
        address.setIsoRegion( "IsoRegion" );
        address.setCountry( "Country" );
        address.setRegion( "Region" );
        allUserFields.add( new UserField( UserFieldType.ADDRESS, address ) );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( new UserStoreUserFieldConfig( UserFieldType.FIRST_NAME ) );
        userStoreConfig.addUserFieldConfig( new UserStoreUserFieldConfig( UserFieldType.LAST_NAME ) );
        UserStoreUserFieldConfig addressConfig = new UserStoreUserFieldConfig( UserFieldType.ADDRESS );
        addressConfig.setIso( false );
        userStoreConfig.addUserFieldConfig( addressConfig );

        // exercise
        UserFields configuredUserFieldsOnly = allUserFields.getConfiguredFieldsOnly( userStoreConfig );

        // verify
        assertEquals( 3, configuredUserFieldsOnly.getSize() );
        assertEquals( "First name", configuredUserFieldsOnly.getField( UserFieldType.FIRST_NAME ).getValue() );
        assertEquals( "Last name", configuredUserFieldsOnly.getField( UserFieldType.LAST_NAME ).getValue() );
        assertEquals( null, configuredUserFieldsOnly.getField( UserFieldType.PHONE ) );
        assertEquals( "Label", configuredUserFieldsOnly.getField( UserFieldType.ADDRESS ).getValueAsAddress().getLabel() );
        assertEquals( "Country", configuredUserFieldsOnly.getField( UserFieldType.ADDRESS ).getValueAsAddress().getCountry() );
        assertEquals( "Region", configuredUserFieldsOnly.getField( UserFieldType.ADDRESS ).getValueAsAddress().getRegion() );
        assertEquals( null, configuredUserFieldsOnly.getField( UserFieldType.ADDRESS ).getValueAsAddress().getIsoCountry() );
        assertEquals( null, configuredUserFieldsOnly.getField( UserFieldType.ADDRESS ).getValueAsAddress().getIsoRegion() );
    }

    @Test
    public void getChangedUserFields_given_unequal_photo_then_photo_field_is_returned()
    {
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.PHOTO, new byte[]{101} ) );

        UserFields otherUserFields = new UserFields( false );
        otherUserFields.add( new UserField( UserFieldType.PHOTO, new byte[]{123} ) );

        // exercise
        UserFields changedFields = userFields.getChangedUserFields( otherUserFields, true );

        // verify
        assertEquals( 1, changedFields.getSize() );
        assertNotNull( changedFields.getField( UserFieldType.PHOTO ) );
    }

    @Test
    public void getChangedUserFields_given_equal_photo_then_no_fields_are_returned()
    {
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.PHOTO, new byte[]{101} ) );

        UserFields otherUserFields = new UserFields( false );
        otherUserFields.add( new UserField( UserFieldType.PHOTO, new byte[]{101} ) );

        // exercise
        UserFields changedFields = userFields.getChangedUserFields( otherUserFields, true );

        // verify
        assertEquals( 0, changedFields.getSize() );
    }

    @Test
    public void getChangedUserFields_given_equal_birthday_then_no_fields_are_returned()
    {
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );

        UserFields otherUserFields = new UserFields( false );
        otherUserFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );

        // exercise
        UserFields changedFields = userFields.getChangedUserFields( otherUserFields, true );

        // verify
        assertEquals( 0, changedFields.getSize() );
    }

    @Test
    public void getChangedUserFields_given_unequal_birthday_then_birthday_field_is_returned()
    {
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 1, 19 ).toDate() ) );

        UserFields otherUserFields = new UserFields( false );
        otherUserFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );

        // exercise
        UserFields changedFields = userFields.getChangedUserFields( otherUserFields, true );

        // verify
        assertEquals( 1, changedFields.getSize() );
        assertNotNull( changedFields.getField( UserFieldType.BIRTHDAY ) );
    }

    @Test
    public void add_given_two_addresses_when_multiple_addresses_is_false_then_only_one_is_returned()
    {
        UserFields userFields = new UserFields( false );
        Address address1 = new Address();
        address1.setLabel( "Address 1" );
        userFields.add( new UserField( UserFieldType.ADDRESS, address1 ) );

        Address address2 = new Address();
        address2.setLabel( "Address 2" );
        userFields.add( new UserField( UserFieldType.ADDRESS, address2 ) );

        // exercise
        List<Address> addresses = userFields.getAddresses();

        // verify
        assertEquals( 1, addresses.size() );
        assertEquals( "Address 1", addresses.get( 0 ).getLabel() );
    }

    @Test
    public void getAddresses()
    {
        UserFields userFields = new UserFields( true );
        Address address1 = new Address();
        address1.setLabel( "Address 1" );
        userFields.add( new UserField( UserFieldType.ADDRESS, address1 ) );

        // exercise
        List<Address> addresses = userFields.getAddresses();

        // verify
        assertEquals( 1, addresses.size() );
        assertEquals( "Address 1", addresses.get( 0 ).getLabel() );

        Address address2 = new Address();
        address2.setLabel( "Address 2" );
        userFields.add( new UserField( UserFieldType.ADDRESS, address2 ) );

        // exercise
        addresses = userFields.getAddresses();

        // verify
        assertEquals( 2, addresses.size() );
        assertEquals( "Address 1", addresses.get( 0 ).getLabel() );
        assertEquals( "Address 2", addresses.get( 1 ).getLabel() );

    }

    private UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, boolean remote )
    {
        UserStoreUserFieldConfig config = new UserStoreUserFieldConfig( type );
        config.setRemote( remote );
        return config;
    }
}
