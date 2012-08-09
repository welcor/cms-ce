package com.enonic.cms.core.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.access.ContentAccessType;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.*;

public class ContentACLSynchronizerTest
{
    private int nextContentAccessEntityKey = 1;

    private GroupDao groupDao;

    private final GroupKey group1 = new GroupKey( "A" );

    private final GroupKey group2 = new GroupKey( "B" );

    private final GroupKey group3 = new GroupKey( "C" );

    @Before
    public void before()
    {
        groupDao = Mockito.mock( GroupDao.class );
        Mockito.when( groupDao.findByKey( group1 ) ).thenReturn( createGroup( group1 ) );
        Mockito.when( groupDao.findByKey( group2 ) ).thenReturn( createGroup( group2 ) );
        Mockito.when( groupDao.findByKey( group3 ) ).thenReturn( createGroup( group3 ) );
    }

    @Test
    public void no_modified_ac()
    {
        ContentEntity content = createContent( 1, "c1" );
        content.addContentAccessRight( createContentAccessEntity( true, true, true, createGroup( group1 ) ) );

        // exercise
        ContentACL bluePrint = new ContentACL();
        bluePrint.add( createContentAccessControl( true, true, true, group1 ) );
        ContentACLSynchronizer synchronizer = new ContentACLSynchronizer( groupDao );
        boolean modified = synchronizer.synchronize( content, bluePrint );

        // verify
        assertFalse( modified );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.READ ) );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.UPDATE ) );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.DELETE ) );
    }

    @Test
    public void new_ac()
    {
        ContentEntity content = createContent( 1, "c1" );

        // exercise
        ContentACL bluePrint = new ContentACL();
        bluePrint.add( createContentAccessControl( true, true, true, group1 ) );
        ContentACLSynchronizer synchronizer = new ContentACLSynchronizer( groupDao );
        boolean modified = synchronizer.synchronize( content, bluePrint );

        // verify
        assertTrue( modified );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.READ ) );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.UPDATE ) );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.DELETE ) );
    }

    @Test
    public void remove_ac()
    {
        ContentEntity content = createContent( 1, "c1" );
        content.addContentAccessRight( createContentAccessEntity( true, true, true, createGroup( group1 ) ) );

        // exercise
        ContentACL bluePrint = new ContentACL();
        ContentACLSynchronizer synchronizer = new ContentACLSynchronizer( groupDao );
        boolean modified = synchronizer.synchronize( content, bluePrint );

        // verify
        assertTrue( modified );
        assertNull( content.getContentAccessRight( group1 ) );
    }

    @Test
    public void modify_ac()
    {
        ContentEntity content = createContent( 1, "c1" );
        content.addContentAccessRight( createContentAccessEntity( true, true, true, createGroup( group1 ) ) );

        // exercise
        ContentACL bluePrint = new ContentACL();
        bluePrint.add( createContentAccessControl( true, false, true, group1 ) );
        ContentACLSynchronizer synchronizer = new ContentACLSynchronizer( groupDao );
        boolean modified = synchronizer.synchronize( content, bluePrint );

        // verify
        assertTrue( modified );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.READ ) );
        assertEquals( false, content.hasAccessRightSet( group1, ContentAccessType.UPDATE ) );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.DELETE ) );
    }

    @Test
    public void modified_removed_added_ac()
    {
        ContentEntity content = createContent( 1, "c1" );
        content.addContentAccessRight( createContentAccessEntity( true, true, true, createGroup( group1 ) ) );
        content.addContentAccessRight( createContentAccessEntity( true, false, true, createGroup( group2 ) ) );

        // exercise
        ContentACL bluePrint = new ContentACL();
        bluePrint.add( createContentAccessControl( true, false, false, group1 ) );
        bluePrint.add( createContentAccessControl( false, false, true, group3 ) );
        ContentACLSynchronizer synchronizer = new ContentACLSynchronizer( groupDao );
        boolean modified = synchronizer.synchronize( content, bluePrint );

        // verify modified
        assertTrue( modified );
        assertNotNull( content.getContentAccessRight( group1 ) );
        assertEquals( true, content.hasAccessRightSet( group1, ContentAccessType.READ ) );
        assertEquals( false, content.hasAccessRightSet( group1, ContentAccessType.UPDATE ) );
        assertEquals( false, content.hasAccessRightSet( group1, ContentAccessType.DELETE ) );

        // verify removed
        assertNull( content.getContentAccessRight( group2 ) );

        // verify added
        assertNotNull( content.getContentAccessRight( group3 ) );
        assertEquals( false, content.hasAccessRightSet( group3, ContentAccessType.READ ) );
        assertEquals( false, content.hasAccessRightSet( group3, ContentAccessType.UPDATE ) );
        assertEquals( true, content.hasAccessRightSet( group3, ContentAccessType.DELETE ) );

    }

    private ContentEntity createContent( int key, String name )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( key ) );
        content.setName( name );
        return content;
    }

    private GroupEntity createGroup( GroupKey groupKey )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( groupKey );
        return group;
    }

    private ContentAccessEntity createContentAccessEntity( boolean read, boolean update, boolean delete, GroupEntity group )
    {
        ContentAccessEntity cac = new ContentAccessEntity();
        cac.setKey( "" + ( nextContentAccessEntityKey++ ) );
        cac.setGroup( group );
        cac.setReadAccess( read );
        cac.setUpdateAccess( update );
        cac.setDeleteAccess( delete );
        return cac;
    }

    private ContentAccessControl createContentAccessControl( boolean read, boolean update, boolean delete, GroupKey group )
    {
        ContentAccessControl cac = new ContentAccessControl();
        cac.setGroup( group );
        cac.setRead( read );
        cac.setUpdate( update );
        cac.setDelete( delete );
        return cac;
    }
}

