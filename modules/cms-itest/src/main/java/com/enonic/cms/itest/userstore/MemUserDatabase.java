package com.enonic.cms.itest.userstore;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.cms.api.plugin.ext.userstore.RemoteGroup;
import com.enonic.cms.api.plugin.ext.userstore.RemotePrincipal;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;

@Component
public final class MemUserDatabase
{
    private final Map<String, RemoteUser> users;

    private final Map<String, String> passwords;

    private final Map<String, RemoteGroup> groups;

    private final Multimap<RemoteGroup, RemotePrincipal> members;

    private final Multimap<RemotePrincipal, RemoteGroup> memberships;

    public MemUserDatabase()
    {
        this.users = Maps.newHashMap();
        this.passwords = Maps.newHashMap();
        this.groups = Maps.newHashMap();
        this.members = HashMultimap.create();
        this.memberships = HashMultimap.create();
    }

    public void clear()
    {
        this.users.clear();
        this.passwords.clear();
        this.groups.clear();
        this.members.clear();
        this.memberships.clear();
    }

    public RemoteUser getUser( final String userId )
    {
        return this.users.get( userId );
    }

    public List<RemoteUser> getAllUsers()
    {
        return Lists.newArrayList( this.users.values() );
    }

    public RemoteGroup getGroup( final String groupId )
    {
        return this.groups.get( groupId );
    }

    public List<RemoteGroup> getAllGroups()
    {
        return Lists.newArrayList( this.groups.values() );
    }

    public String getPassword( final String userId )
    {
        return this.passwords.get( userId );
    }

    public void setPassword( final String userId, final String password )
    {
        this.passwords.put( userId, password );
    }

    public boolean addUser( final RemoteUser user )
    {
        if ( this.users.containsKey( user.getId() ) )
        {
            return false;
        }

        this.users.put( user.getId(), new RemoteUser( user ) );
        return true;
    }

    public boolean addGroup( final RemoteGroup group )
    {
        if ( this.groups.containsKey( group.getId() ) )
        {
            return false;
        }

        this.groups.put( group.getId(), new RemoteGroup( group ) );
        return true;
    }

    public boolean updateUser( final RemoteUser user )
    {
        if ( !this.users.containsKey( user.getId() ) )
        {
            return false;
        }

        this.users.put( user.getId(), new RemoteUser( user ) );
        return true;
    }

    public boolean updateGroup( final RemoteGroup group )
    {
        if ( !this.groups.containsKey( group.getId() ) )
        {
            return false;
        }

        this.groups.put( group.getId(), new RemoteGroup( group ) );
        return true;
    }

    public boolean removeUser( final RemoteUser user )
    {
        if ( !this.users.containsKey( user.getId() ) )
        {
            return false;
        }

        this.users.remove( user.getId() );
        this.passwords.remove( user.getId() );
        this.memberships.removeAll( user );
        return true;
    }

    public boolean removeGroup( final RemoteGroup group )
    {
        if ( !this.groups.containsKey( group.getId() ) )
        {
            return false;
        }

        this.groups.remove( group.getId() );
        this.members.removeAll( group );
        this.memberships.removeAll( group );
        return true;
    }

    public void addMember( final RemoteGroup group, final RemotePrincipal member )
    {
        this.members.put( group, member );
        this.memberships.put( member, group );
    }

    public void removeMember( final RemoteGroup group, final RemotePrincipal member )
    {
        this.members.removeAll( group );
        this.memberships.remove( member, group );
    }

    public List<RemotePrincipal> getMembers( final RemoteGroup group )
    {
        return Lists.newArrayList( this.members.get( group ) );
    }

    public List<RemoteGroup> getMemberships( final RemotePrincipal principal )
    {
        return Lists.newArrayList( this.memberships.get( principal ) );
    }
}
