/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.EmailAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.NameAlreadyExistsException;
import com.enonic.cms.core.user.remote.RemoteUser;

public class UserSynchronizer
    extends AbstractBaseUserSynchronizer
{
    protected UserSynchronizer( final UserStoreEntity userStore, final boolean syncMemberships )
    {
        super( null, userStore, true, syncMemberships, true );
    }

    public void synchronizeUser( final String uid )
    {
        final RemoteUser remoteUser = remoteUserStorePlugin.getUser( uid );
        final UserEntity localUser;
        if ( remoteUser != null )
        {
            localUser = findUserBySyncValue( remoteUser.getSync() );
        }
        else
        {
            localUser = findUserByName( uid );
        }

        if ( remoteUser == null )
        {
            if ( localUser == null )
            {
                throw new UserNotFoundException( new QualifiedUsername( userStore.getName(), uid ) );
            }
            else
            {
                deleteUser( localUser );
            }
        }
        else
        {
            if ( localUser == null )
            {
                final String userName = getNameToVerify( localUser, remoteUser );
                if ( nameAlreadyUsedByOtherUser( userName, localUser ) )
                {
                    throw new NameAlreadyExistsException( userStore.getName(), userName );
                }
                final String email = getEmailToVerify( localUser, remoteUser );
                if ( emailAlreadyUsedByOtherUser( email, localUser ) )
                {
                    handleEmailAlreadyUsedByOtherUser( userName, email, localUser );
                }

                createUser( remoteUser, new MemberCache() );
            }
            else
            {
                updateUserLocally( remoteUser, localUser );
            }
        }
    }

    private void updateUserLocally( final RemoteUser remoteUser, final UserEntity localUser )
    {
        final String userName = getNameToVerify( localUser, remoteUser );
        if ( nameAlreadyUsedByOtherUser( userName, localUser ) )
        {
            throw new NameAlreadyExistsException( userStore.getName(), userName );
        }

        final String email = getEmailToVerify( localUser, remoteUser );

        if ( emailAlreadyUsedByOtherUser( email, localUser ) )
        {
            handleEmailAlreadyUsedByOtherUser( userName, email, localUser );
        }

        final MemberCache memberCache = new MemberCache();
        updateAndResurrectUser( localUser, remoteUser );

        if ( syncMemberships )
        {
            syncUserMemberships( localUser, remoteUser, memberCache );
        }
    }

    private void handleEmailAlreadyUsedByOtherUser( String userName, String email, UserEntity localUser )
    {
        synchronizeOtherUserWithSameEmail( email, localUser );

        if ( emailAlreadyUsedByOtherUser( email, localUser ) )
        {
            throw new EmailAlreadyExistsException( userStore.getName(), userName, email );
        }
    }
}
