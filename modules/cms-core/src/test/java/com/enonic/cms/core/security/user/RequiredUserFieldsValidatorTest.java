/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.user;


import org.junit.Test;

import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfigField;
import com.enonic.cms.api.plugin.ext.userstore.UserField;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserFields;

public class RequiredUserFieldsValidatorTest
{
    @Test(expected = MissingRequiredUserFieldException.class)
    public void validateAllRequiredFieldsArePresentAndNotEmpty_given_field_that_is_not_required_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.LAST_NAME, "Last name" ) );
        validator.validateAllRequiredFieldsArePresentAndNotEmpty( userFields );
    }

    @Test(expected = MissingRequiredUserFieldException.class)
    public void validateAllRequiredFieldsArePresentAndNotEmpty_given_no_fields_when_one_is_required_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        validator.validateAllRequiredFieldsArePresentAndNotEmpty( userFields );
    }

    @Test(expected = MissingRequiredUserFieldException.class)
    public void validateAllRequiredFieldsArePresentAndNotEmpty_given_field_with_empty_value_when_it_is_required_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        validator.validateAllRequiredFieldsArePresentAndNotEmpty( userFields );
    }

    @Test(expected = MissingRequiredUserFieldException.class)
    public void validateAllRequiredFieldsArePresentAndNotEmpty_given_photo_with_zero_bytes_when_it_is_required_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.PHOTO, "required" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.PHOTO, new byte[]{} ) );
        validator.validateAllRequiredFieldsArePresentAndNotEmpty( userFields );
    }

    @Test()
    public void validateAllRequiredFieldsArePresentAndNotEmpty_given_field_that_is_required_then_exception_is_not_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.FIRST_NAME, "First name" ) );
        validator.validateAllRequiredFieldsArePresentAndNotEmpty( userFields );
    }

    @Test()
    public void validateAllRequiredFieldsArePresentAndNotEmpty_given_no_fields_when_none_is_required_then_exception_is_not_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        validator.validateAllRequiredFieldsArePresentAndNotEmpty( userFields );
    }

    @Test(expected = MissingRequiredUserFieldException.class)
    public void validatePresentFieldsAreNotBlankIfRequired_given_field_with_blank_value_that_is_required_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.FIRST_NAME, "" ) );
        userFields.add( new UserField( UserFieldType.LAST_NAME, "Last name" ) );
        validator.validatePresentFieldsAreNotBlankIfRequired( userFields );
    }

    @Test(expected = MissingRequiredUserFieldException.class)
    public void validatePresentFieldsAreNotBlankIfRequired_given_photo_zero_bytes_that_is_required_then_exception_is_thrown()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.PHOTO, "required" ) );

        RequiredUserFieldsValidator validator = new RequiredUserFieldsValidator( userStoreConfig );
        UserFields userFields = new UserFields( false );
        userFields.add( new UserField( UserFieldType.PHOTO, new byte[]{} ) );
        validator.validatePresentFieldsAreNotBlankIfRequired( userFields );
    }

    private UserStoreConfigField createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreConfigField config = new UserStoreConfigField( type );
        config.setRemote( properties.contains( "remote" ) );
        config.setRequired( properties.contains( "required" ) );
        config.setReadOnly( properties.contains( "read-only" ) );
        return config;
    }
}
