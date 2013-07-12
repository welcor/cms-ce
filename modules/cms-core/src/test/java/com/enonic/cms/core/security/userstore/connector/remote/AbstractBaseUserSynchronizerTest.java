/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.api.plugin.userstore.RemoteUser;
import com.enonic.cms.store.dao.UserDao;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Sep 4, 2009
 */
public class AbstractBaseUserSynchronizerTest
{
    private AbstractBaseUserSynchronizer abstractBaseUserSynchronizer;

    private UserDao userDao;

    private UserStoreEntity userStore;

    private int lastUserKeyCounter;

    @Before
    public void setUp()
    {
        userStore = createUserStore( 1 );
        abstractBaseUserSynchronizer = new UserSynchronizer( userStore, false );

        userDao = createMock( UserDao.class );

        abstractBaseUserSynchronizer.setUserDao( userDao );
    }


    @Test
    public void emailAlreadyUsedByOtherUser_returns_false_when_user_email_is_unique()
    {
        UserEntity localUser = createUser( "rmy", "rmy@enonic.com", userStore, false );
        RemoteUser remoteUser = createRemoteUser( "rmy" );

        List<UserEntity> matchingUsers = Lists.newArrayList( localUser );
        expect( userDao.findBySpecification( isA( UserSpecification.class ) ) ).andReturn( matchingUsers ).anyTimes();
        replay( userDao );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertFalse( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( email, localUser ) );
    }

    @Test
    public void emailAlreadyUsedByOtherUser_returns_false_when_other_user_with_same_email_is_deleted()
    {
        UserEntity localUser = createUser( "rmy", "rmy@enoni.com", userStore, false );
        RemoteUser remoteUser = createRemoteUser( "rmy" );

        List<UserEntity> matchingUsers = Lists.newArrayList( localUser );
        expect( userDao.findBySpecification( isA( UserSpecification.class ) ) ).andReturn( matchingUsers ).anyTimes();
        replay( userDao );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertFalse( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( email, localUser ) );
    }

    @Test
    public void emailAlreadyUsedByOtherUser_returns_true_when_other_user_with_same_email_exists()
    {
        RemoteUser remoteUser = createRemoteUser( "rmy" );
        UserEntity localUser = createUser( "rmy", "rmy@enoni.com", userStore, false );

        List<UserEntity> matchingUsers = Lists.newArrayList( localUser, createUser( "extrmy", "rmy@enoni.com", userStore, false ) );
        expect( userDao.findBySpecification( isA( UserSpecification.class ) ) ).andReturn( matchingUsers ).anyTimes();
        replay( userDao );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertTrue( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( email, localUser ) );
    }

    @Test
    public void emailAlreadyUsedByOtherUser_returns_true_when_same_user_exists_twice_with_same_email()
    {
        RemoteUser remoteUser = createRemoteUser( "rmy" );
        UserEntity localUser = createUser( "rmy", "rmy@enoni.com", userStore, false );

        List<UserEntity> matchingUsers = Lists.newArrayList( localUser, createUser( "rmy", "rmy@enoni.com", userStore, false ) );
        expect( userDao.findBySpecification( isA( UserSpecification.class ) ) ).andReturn( matchingUsers ).anyTimes();
        replay( userDao );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertTrue( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( email, localUser ) );
    }

    private UserEntity createUser( String uid, String email, UserStoreEntity userStore, boolean isDeleted )
    {
        UserEntity user = new UserEntity();

        UserKey userKey = new UserKey( uid + ( lastUserKeyCounter++ ) );
        user.setKey( userKey );
        user.setName( uid );
        user.setEmail( email );
        user.setUserStore( userStore );
        user.setDeleted( isDeleted );

        return user;
    }

    private RemoteUser createRemoteUser( String id )
    {
        RemoteUser user = new RemoteUser( id );
        user.setEmail( id + "@enonic" );

        return user;
    }

    private UserStoreEntity createUserStore( int key )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( key ) );
        userStore.setName( "myUserStore" + key );
        return userStore;
    }
}
