/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.security.userstore;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.GroupAlreadyExistsException;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class UserStoreServiceImpl_globalGroupTest
    extends AbstractSpringTest
{

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private UserStoreService userStoreService;

    @Before
    public void setUp()
        throws Exception
    {
        fixture.initSystemData();

    }

    @Test
    public void storeNewGroup()
        throws Exception
    {
        // exercise:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.GLOBAL_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        GroupEntity actualGroup = fixture.findGroupByKey( groupKey );

        // verify:
        assertEquals( "myGroup", actualGroup.getName() );
        assertEquals( "Description", actualGroup.getDescription() );
        assertEquals( GroupType.GLOBAL_GROUP, actualGroup.getType() );
    }

    @Test
    public void storeNewGroup_given_group_with_name_that_is_already_used_then_exception_is_thrown()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.GLOBAL_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        userStoreService.storeNewGroup( storeNewGroupCommand );

        // exercise & verify
        try
        {
            userStoreService.storeNewGroup( storeNewGroupCommand );
            fail( "Exception expected" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof GroupAlreadyExistsException );
        }
    }

    @Test
    public void storeNewGroup_given_group_with_name_that_is_already_used_but_is_deleted_then_new_group_is_stored()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.GLOBAL_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        DeleteGroupCommand deleteGroupCommand =
            new DeleteGroupCommand( fixture.findUserByName( "admin" ), GroupSpecification.usingKey( groupKey ) );
        userStoreService.deleteGroup( deleteGroupCommand );
        assertEquals( true, fixture.findGroupByKey( groupKey ).isDeleted() );

        // exercise & verify
        GroupKey newGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertNotNull( newGroupKey );
        assertFalse( groupKey.equals( newGroupKey ) );
        assertEquals( false, fixture.findGroupByKey( newGroupKey ).isDeleted() );
    }

    @Test
    public void deleteGroup()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.GLOBAL_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertEquals( 1, fixture.countGroupsByType( GroupType.GLOBAL_GROUP ) );

        // exercise
        GroupSpecification groupToDeleteSpec = new GroupSpecification();
        groupToDeleteSpec.setKey( groupKey );
        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand( fixture.findUserByName( "admin" ), groupToDeleteSpec );
        userStoreService.deleteGroup( deleteGroupCommand );

        // verify:
        GroupEntity deletedGroup = fixture.findGroupByKey( groupKey );
        assertEquals( true, deletedGroup.isDeleted() );
    }

    @Test
    public void updateGroup()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.GLOBAL_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertEquals( 1, fixture.countGroupsByType( GroupType.GLOBAL_GROUP ) );

        // exercise
        UpdateGroupCommand updateGroupCommand = new UpdateGroupCommand( fixture.findUserByName( "admin" ).getKey(), groupKey );
        updateGroupCommand.setName( "myChange" );
        updateGroupCommand.setDescription( "Changed" );
        userStoreService.updateGroup( updateGroupCommand );

        // verify:
        assertEquals( 1, fixture.countGroupsByType( GroupType.GLOBAL_GROUP ) );
        GroupEntity updatedGroup = fixture.findGroupByKey( groupKey );
        assertEquals( false, updatedGroup.isDeleted() );
        assertEquals( "myChange", updatedGroup.getName() );
        assertEquals( "Changed", updatedGroup.getDescription() );
    }

}
