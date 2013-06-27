/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.user;


import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.core.user.field.UserInfoTransformer;

public class ReadOnlyUserFieldValidator
{
    private UserStoreConfig userStoreConfig;

    public ReadOnlyUserFieldValidator( UserStoreConfig userStoreConfig )
    {
        this.userStoreConfig = userStoreConfig;
    }

    public void validate( UserInfo userInfo )
    {
        validate( new UserInfoTransformer().toUserFields( userInfo ) );
    }

    public void validate( UserFields userFields )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userStoreConfig.getUserFieldConfigs() )
        {
            if ( userFieldConfig.isReadOnly() && userFields.hasField( userFieldConfig.getType() ) )
            {
                throw new ReadOnlyUserFieldPolicyException( userFieldConfig.getType() );
            }
        }
    }
}
