/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.EmailAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.NameAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;

public class UsersSynchronizer
    extends AbstractBaseUserSynchronizer
{
    private static final Logger LOG = LoggerFactory.getLogger( UsersSynchronizer.class );

    public void setStatusCollector( final SynchronizeStatus value )
    {
        status = value;
    }

    protected UsersSynchronizer( final SynchronizeStatus synchronizeStatus, final UserStoreEntity userStore, final boolean syncUser,
                                 final boolean syncMemberships )
    {
        super( synchronizeStatus, userStore, syncUser, syncMemberships, false );
    }

    public void synchronizeUsers( final List<RemoteUser> remoteUsers, final MemberCache memberCache )
    {
        for ( final RemoteUser remoteUser : remoteUsers )
        {
            createUpdateOrResurrectLocalUser( remoteUser, memberCache );
        }
    }

    private void createUpdateOrResurrectLocalUser( final RemoteUser remoteUser, final MemberCache memberCache )
    {
        UserEntity localUser = findUserBySyncValue( remoteUser.getSync() );

        if ( syncUser )
        {
            if ( !canBeCreatedOrUpdated( localUser, remoteUser ) )
            {
                status.userSkipped();
                return;
            }

            if ( localUser == null )
            {
                localUser = createUser( remoteUser, memberCache );
                status.userCreated();
            }
            else
            {
                final boolean resurrected = updateAndResurrectUser( localUser, remoteUser );
                status.userUpdated( resurrected );
            }
        }
        if ( syncMemberships && localUser != null )
        {
            syncUserMemberships( localUser, remoteUser, memberCache );
        }
    }

    private boolean canBeCreatedOrUpdated( final UserEntity localUser, final RemoteUser remoteUser )
    {
        final String userName = getNameToVerify( localUser, remoteUser );
        if ( nameAlreadyUsedByOtherUser( userName, localUser ) )
        {
            LOG.warn( NameAlreadyExistsException.createMessage( userStore.getName(), userName ) );
            return false;
        }

        final String email = getEmailToVerify( localUser, remoteUser );
        if ( emailAlreadyUsedByOtherUser( email, localUser ) )
        {
            synchronizeOtherUserWithSameEmail( email, localUser );

            if ( emailAlreadyUsedByOtherUser( email, localUser ) )
            {
                LOG.warn( EmailAlreadyExistsException.createMessage( userStore.getName(), userName, email ) );
                return false;
            }
        }

        return true;
    }
}
