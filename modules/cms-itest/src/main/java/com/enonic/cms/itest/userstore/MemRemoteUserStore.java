package com.enonic.cms.itest.userstore;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.cms.api.plugin.ext.userstore.RemoteGroup;
import com.enonic.cms.api.plugin.ext.userstore.RemotePrincipal;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;

final class MemRemoteUserStore
    implements RemoteUserStore
{
    private final MemUserDatabase database;

    public MemRemoteUserStore( final MemUserDatabase database )
    {
        this.database = database;
    }

    @Override
    public Set<UserFieldType> getSupportedFieldTypes()
    {
        return Sets.newHashSet( UserFieldType.values() );
    }

    @Override
    public boolean authenticate( final String userId, final String password )
    {
        final String storedPassword = this.database.getPassword( userId );
        return storedPassword != null && storedPassword.equals( password );
    }

    @Override
    public RemoteUser getUser( final String userId )
    {
        return this.database.getUser( userId );
    }

    @Override
    public List<RemoteUser> getAllUsers()
    {
        return this.database.getAllUsers();
    }

    @Override
    public RemoteGroup getGroup( final String groupId )
    {
        return this.database.getGroup( groupId );
    }

    @Override
    public List<RemoteGroup> getAllGroups()
    {
        return this.database.getAllGroups();
    }

    @Override
    public boolean changePassword( final String userId, final String password )
    {
        if ( this.database.getUser( userId ) != null )
        {
            this.database.setPassword( userId, password );
            return true;
        }

        return false;
    }

    @Override
    public boolean addPrincipal( final RemotePrincipal principal )
    {
        if ( principal instanceof RemoteUser )
        {
            return addUser( (RemoteUser) principal );
        }
        else if ( principal instanceof RemoteGroup )
        {
            return addGroup( (RemoteGroup) principal );
        }
        else
        {
            return false;
        }
    }

    private boolean addUser( final RemoteUser user )
    {
        this.database.setPassword( user.getId(), "password" );
        return this.database.addUser( user );
    }

    private boolean addGroup( final RemoteGroup group )
    {
        return this.database.addGroup( group );
    }

    @Override
    public boolean updatePrincipal( final RemotePrincipal principal )
    {
        if ( principal instanceof RemoteUser )
        {
            return updateUser( (RemoteUser) principal );
        }
        else if ( principal instanceof RemoteGroup )
        {
            return updateGroup( (RemoteGroup) principal );
        }
        else
        {
            return false;
        }
    }

    private boolean updateUser( final RemoteUser user )
    {
        return this.database.updateUser( user );
    }

    private boolean updateGroup( final RemoteGroup group )
    {
        return this.database.updateGroup( group );
    }

    @Override
    public boolean removePrincipal( final RemotePrincipal principal )
    {
        if ( principal instanceof RemoteUser )
        {
            return removeUser( (RemoteUser) principal );
        }
        else if ( principal instanceof RemoteGroup )
        {
            return removeGroup( (RemoteGroup) principal );
        }
        else
        {
            return false;
        }
    }

    private boolean removeUser( final RemoteUser user )
    {
        return this.database.removeUser( user );
    }

    private boolean removeGroup( final RemoteGroup group )
    {
        return this.database.removeGroup( group );
    }

    @Override
    public List<RemotePrincipal> getMembers( final RemoteGroup group )
    {
        return this.database.getMembers( group );
    }

    @Override
    public void addMembers( final RemoteGroup group, final List<RemotePrincipal> members )
    {
        for ( final RemotePrincipal member : members )
        {
            addMember( group, member );
        }
    }

    @Override
    public void removeMembers( final RemoteGroup group, final List<RemotePrincipal> members )
    {
        for ( final RemotePrincipal member : members )
        {
            removeMember( group, member );
        }
    }

    @Override
    public List<RemoteGroup> getMemberships( final RemotePrincipal principal )
    {
        return this.database.getMemberships( principal );
    }

    private void addMember( final RemoteGroup group, final RemotePrincipal member )
    {
        this.database.addMember( group, member );
    }

    private void removeMember( final RemoteGroup group, final RemotePrincipal member )
    {
        this.database.removeMember( group, member );
    }
}
