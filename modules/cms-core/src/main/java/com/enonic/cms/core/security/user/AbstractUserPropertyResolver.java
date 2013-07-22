/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.api.plugin.userstore.UserStoreConfig;
import com.enonic.cms.api.plugin.userstore.UserFieldType;
import com.enonic.cms.api.plugin.userstore.UserFields;

/**
 * Created by rmy - Date: Sep 18, 2009
 */
public class AbstractUserPropertyResolver
{
    protected final UserStoreConfig userStoreConfig;

    protected String prefix = "";

    protected String suffix = "";

    protected String initials = "";

    protected String firstName = "";

    protected String middleName = "";

    protected String lastName = "";

    protected String nickName = "";

    protected String displayName;

    protected String userName;

    AbstractUserPropertyResolver( UserStoreConfig userStoreConfig )
    {
        this.userStoreConfig = userStoreConfig;
    }

    protected void setUserInfoFields( UserFields userFields )
    {
        if ( isUserFieldActive( UserFieldType.PREFIX ) )
        {
            this.prefix = userFields.getPrefix();
        }

        if ( isUserFieldActive( UserFieldType.FIRST_NAME ) )
        {
            this.firstName = userFields.getFirstName();
        }

        if ( isUserFieldActive( UserFieldType.MIDDLE_NAME ) )
        {
            this.middleName = userFields.getMiddleName();
        }

        if ( isUserFieldActive( UserFieldType.LAST_NAME ) )
        {
            this.lastName = userFields.getLastName();
        }

        if ( isUserFieldActive( UserFieldType.SUFFIX ) )
        {
            this.suffix = userFields.getSuffix();
        }

        if ( isUserFieldActive( UserFieldType.NICK_NAME ) )
        {
            this.nickName = userFields.getNickName();
        }

        if ( isUserFieldActive( UserFieldType.INITIALS ) )
        {
            this.initials = userFields.getInitials();
        }
    }

    protected String resolveFrom( final String... parts )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final String part : parts )
        {
            if ( part != null && part.trim().length() > 0 )
            {
                if ( builder.length() > 0 )
                {
                    builder.append( " " );
                }
                builder.append( part.trim() );
            }
        }
        return builder.toString();
    }

    private boolean isUserFieldActive( UserFieldType type )
    {
        return userStoreConfig.getUserFieldConfig( type ) != null;
    }
}
