/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.plugin.userstore;

import java.util.List;
import java.util.Set;

public interface RemoteUserStore
{
    public Set<UserFieldType> getSupportedFieldTypes();

    public boolean authenticate( String userId, String password );

    public RemoteUser getUser( String userId );

    public List<RemoteUser> getAllUsers();

    public RemoteGroup getGroup( String groupId );

    public List<RemoteGroup> getAllGroups();

    public boolean changePassword( String userId, String password );

    public boolean addPrincipal( RemotePrincipal principal );

    public boolean updatePrincipal( RemotePrincipal principal );

    public boolean removePrincipal( RemotePrincipal principal );

    public List<RemotePrincipal> getMembers( RemoteGroup group );

    public void addMembers( RemoteGroup group, List<RemotePrincipal> members );

    public void removeMembers( RemoteGroup group, List<RemotePrincipal> members );

    public List<RemoteGroup> getMemberships( RemotePrincipal principal );
}
