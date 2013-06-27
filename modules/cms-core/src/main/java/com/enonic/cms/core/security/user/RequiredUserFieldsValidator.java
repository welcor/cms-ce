/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.user;


import org.apache.commons.lang.StringUtils;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.core.user.field.UserInfoTransformer;

public class RequiredUserFieldsValidator
{
    private UserStoreConfig userStoreConfig;

    public RequiredUserFieldsValidator( UserStoreConfig userStoreConfig )
    {
        this.userStoreConfig = userStoreConfig;
    }

    public void validateAllRequiredFieldsArePresentAndNotEmpty( final UserInfo userInfo )
    {
        validateAllRequiredFieldsArePresentAndNotEmpty( new UserInfoTransformer().toUserFields( userInfo ) );
    }

    public void validateAllRequiredFieldsArePresentAndNotEmpty( final UserFields userFields )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userStoreConfig.getUserFieldConfigs() )
        {
            final UserFieldType type = userFieldConfig.getType();
            if ( userFieldConfig.isRequired() )
            {
                final UserField field = userFields.getField( type );
                if ( field == null || isBlank( field ) )
                {
                    throw new MissingRequiredUserFieldException( type );
                }
            }
        }
    }

    public void validatePresentFieldsAreNotBlankIfRequired( final UserFields userFields )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userStoreConfig.getUserFieldConfigs() )
        {
            final UserFieldType type = userFieldConfig.getType();

            if ( userFieldConfig.isRequired() )
            {
                final UserField field = userFields.getField( type );
                if ( field != null && isBlank( field ) )
                {
                    throw new MissingRequiredUserFieldException( type );
                }
            }
        }
    }

    private boolean isBlank( final UserField userField )
    {
        Object value = userField.getValue();
        if ( value instanceof String )
        {
            return StringUtils.isBlank( (String) value );
        }
        else if ( value instanceof byte[] )
        {
            return ( (byte[]) value ).length == 0;
        }

        return value == null;
    }
}
