/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.group;


import com.enonic.cms.core.security.user.UserEntity;

public class GroupFactory
{
    public static GroupEntity createUserGroup( UserEntity user )
    {
        GroupEntity userGroup = new GroupEntity();
        userGroup.setDeleted( 0 );
        userGroup.setDescription( null );
        userGroup.setName( "userGroup" + user.getKey() );
        userGroup.setSyncValue( user.getSync() );
        userGroup.setUser( user );
        userGroup.setUserStore( user.getUserStore() );
        userGroup.setType( GroupType.USER );
        userGroup.setRestricted( 1 );
        return userGroup;
    }

}
