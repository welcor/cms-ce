package com.enonic.cms.core.security.user;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;

public class ReadOnlyUserFieldValidatorTest
{
    @Test(expected = ReadOnlyUserFieldPolicyException.class)
    public void validate_given_field_photo_when_photo_is_readOnly_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.PHOTO, "read-only" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.PHOTO, new byte[]{} ) );
        validator.validate( userFields );
    }

    @Test(expected = ReadOnlyUserFieldPolicyException.class)
    public void validate_given_field_when_field_is_readOnly_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "read-only" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.ORGANIZATION, "My organization" ) );
        validator.validate( userFields );
    }

    @Test(expected = ReadOnlyUserFieldPolicyException.class)
    public void validate_given_birhtday_when_field_is_readOnly_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "read-only" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );
        validator.validate( userFields );
    }

    @Test(expected = ReadOnlyUserFieldPolicyException.class)
    public void validate_given_gender_when_field_is_readOnly_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.GENDER, "read-only" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.GENDER, Gender.FEMALE ) );
        validator.validate( userFields );
    }

    @Test(expected = ReadOnlyUserFieldPolicyException.class)
    public void validate_given_address_when_field_is_readOnly_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ADDRESS, "read-only" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.ADDRESS, new Address() ) );
        validator.validate( userFields );
    }

    @Test
    public void validate_given_field_when_field_is_not_readOnly_then_exception_is_not_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.ORGANIZATION, "My organization" ) );
        validator.validate( userFields );
    }

    @Test
    public void validate_given_birhtday_when_field_is_not_readOnly_then_exception_is_not_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, new DateMidnight( 1976, 4, 19 ).toDate() ) );
        validator.validate( userFields );
    }

    @Test
    public void validate_given_gender_when_field_is_not_readOnly_then_exception_is_not_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.GENDER, "" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.GENDER, Gender.FEMALE ) );
        validator.validate( userFields );
    }

    @Test
    public void validate_given_address_when_field_is_not_readOnly_then_exception_is_not_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ADDRESS, "" ) );

        ReadOnlyUserFieldValidator validator = new ReadOnlyUserFieldValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.ADDRESS, new Address() ) );
        validator.validate( userFields );
    }

    private UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreUserFieldConfig config = new UserStoreUserFieldConfig( type );
        config.setRemote( properties.contains( "remote" ) );
        config.setRequired( properties.contains( "required" ) );
        config.setReadOnly( properties.contains( "read-only" ) );
        return config;
    }
}
