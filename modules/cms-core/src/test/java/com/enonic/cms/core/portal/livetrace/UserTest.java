package com.enonic.cms.core.portal.livetrace;


import org.junit.Test;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.userstore.UserStoreKey;

import static org.junit.Assert.*;

public class UserTest
{
    @Test
    public void given_admin_user()
    {
        User user = User.createUser( new QualifiedUsername( "admin" ) );
        assertEquals( "admin", user.getUserName() );
        assertEquals( null, user.getUserStoreName() );
    }

    @Test
    public void given_admin_anonymous()
    {
        User user = User.createUser( new QualifiedUsername( "anonymous" ) );
        assertEquals( "anonymous", user.getUserName() );
        assertEquals( null, user.getUserStoreName() );
    }

    @Test
    public void given_user_in_userstore()
    {
        User user = User.createUser( new QualifiedUsername( "myuserstore", "myuser" ) );
        assertEquals( "myuser", user.getUserName() );
        assertEquals( "myuserstore", user.getUserStoreName() );
    }

    @Test
    public void given_user_in_userstore_specified_by_key()
    {
        User user = User.createUser( new QualifiedUsername( new UserStoreKey( "1" ), "myuser" ) );
        assertEquals( "myuser", user.getUserName() );
        assertEquals( "1", user.getUserStoreName() );
    }
}
