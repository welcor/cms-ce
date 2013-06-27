/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

import static org.junit.Assert.*;


public class UnitEntityTest
{
    @Test
    public void synchronizeContentTypes_one_added()
        throws Exception
    {
        // setup
        UnitEntity unit = new UnitEntity();

        // exercise
        Set<ContentTypeEntity> contentTypes = new LinkedHashSet<ContentTypeEntity>();
        contentTypes.add( createContentType( 1 ) );
        boolean modified = unit.synchronizeContentTypes( contentTypes );

        // verify
        assertTrue( modified );
        assertEquals( Lists.newArrayList( createContentType( 1 ) ), Lists.newArrayList( unit.getContentTypes() ) );
    }

    @Test
    public void synchronizeContentTypes_no_changes()
        throws Exception
    {
        // setup
        UnitEntity unit = new UnitEntity();
        unit.addContentType( createContentType( 1 ) );
        unit.addContentType( createContentType( 2 ) );

        // exercise
        Set<ContentTypeEntity> contentTypes = new LinkedHashSet<ContentTypeEntity>();
        contentTypes.add( createContentType( 1 ) );
        contentTypes.add( createContentType( 2 ) );
        boolean modified = unit.synchronizeContentTypes( contentTypes );

        // verify
        assertFalse( modified );
        assertEquals( Lists.newArrayList( createContentType( 1 ), createContentType( 2 ) ), Lists.newArrayList( unit.getContentTypes() ) );
    }

    @Test
    public void synchronizeContentTypes_one_removed()
        throws Exception
    {
        // setup
        UnitEntity unit = new UnitEntity();
        unit.addContentType( createContentType( 1 ) );
        unit.addContentType( createContentType( 2 ) );

        // exercise
        Set<ContentTypeEntity> contentTypes = new LinkedHashSet<ContentTypeEntity>();
        contentTypes.add( createContentType( 2 ) );
        boolean modified = unit.synchronizeContentTypes( contentTypes );

        // verify
        assertTrue( modified );
        assertEquals( Lists.newArrayList( createContentType( 2 ) ), Lists.newArrayList( unit.getContentTypes() ) );
    }

    @Test
    public void synchronizeContentTypes_all_removed()
        throws Exception
    {
        // setup
        UnitEntity unit = new UnitEntity();
        unit.addContentType( createContentType( 1 ) );
        unit.addContentType( createContentType( 2 ) );

        // exercise
        Set<ContentTypeEntity> contentTypes = new LinkedHashSet<ContentTypeEntity>();
        boolean modified = unit.synchronizeContentTypes( contentTypes );

        // verify
        assertTrue( modified );
        assertEquals( Lists.newArrayList(), Lists.newArrayList( unit.getContentTypes() ) );
    }

    @Test
    public void synchronizeContentTypes_one_added_one_removed()
        throws Exception
    {
        // setup
        UnitEntity unit = new UnitEntity();
        unit.addContentType( createContentType( 1 ) );
        unit.addContentType( createContentType( 2 ) );

        // exercise
        Set<ContentTypeEntity> contentTypes = new LinkedHashSet<ContentTypeEntity>();
        contentTypes.add( createContentType( 1 ) );
        contentTypes.add( createContentType( 3 ) );
        boolean modified = unit.synchronizeContentTypes( contentTypes );

        // verify
        assertTrue( modified );
        assertEquals( Lists.newArrayList( createContentType( 1 ), createContentType( 3 ) ), Lists.newArrayList( unit.getContentTypes() ) );
    }

    private ContentTypeEntity createContentType( int key )
    {
        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setKey( key );
        return contentType;
    }
}
