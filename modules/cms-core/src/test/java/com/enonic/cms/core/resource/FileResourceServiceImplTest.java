/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.util.List;

import com.enonic.cms.framework.util.MimeTypeResolver;

public class FileResourceServiceImplTest
{
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private FileResourceServiceImpl fileService;

    @Before
    public void setUp()
        throws Exception
    {
        final MimeTypeResolver mimeTypeResolver = Mockito.mock( MimeTypeResolver.class );
        Mockito.when( mimeTypeResolver.getMimeType( "c.txt" ) ).thenReturn( "text/plain" );

        this.fileService = new FileResourceServiceImpl();
        this.fileService.setMimeTypeResolver( mimeTypeResolver );
        this.fileService.setResourceRoot( this.tmpFolder.newFolder() );
    }

    @Test
    public void testRootResource()
    {
        FileResource res = this.fileService.getResource( new FileResourceName( "/" ) );
        Assert.assertNotNull( res );
        Assert.assertTrue( res.isFolder() );
        Assert.assertEquals( "/", res.getName().getPath() );
    }

    @Test
    public void testCreateFolder()
    {
        Assert.assertTrue( this.fileService.createFolder( new FileResourceName( "/a/b/c" ) ) );
        FileResource res = this.fileService.getResource( new FileResourceName( "/a/b/c" ) );
        Assert.assertNotNull( res );
        Assert.assertTrue( res.isFolder() );
        Assert.assertEquals( "/a/b/c", res.getName().getPath() );

        Assert.assertFalse( this.fileService.createFolder( new FileResourceName( "/a/b/c" ) ) );
        res = this.fileService.getResource( new FileResourceName( "/a/b/c" ) );
        Assert.assertNotNull( res );
    }

    @Test
    public void testCreateFile()
    {
        Assert.assertTrue( this.fileService.createFile( new FileResourceName( "/a/b/c.txt" ), null ) );
        FileResource res = this.fileService.getResource( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNotNull( res );
        Assert.assertFalse( res.isFolder() );
        Assert.assertEquals( "/a/b/c.txt", res.getName().getPath() );
        Assert.assertEquals( "text/plain", res.getMimeType() );
        Assert.assertEquals( 0, res.getSize() );
        Assert.assertNotNull( res.getLastModified() );

        Assert.assertFalse( this.fileService.createFile( new FileResourceName( "/a/b/c.txt" ), null ) );
        res = this.fileService.getResource( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNotNull( res );

        Assert.assertTrue( this.fileService.createFile( new FileResourceName( "/a/b/d.txt" ), FileResourceData.create( new byte[2] ) ) );
        res = this.fileService.getResource( new FileResourceName( "/a/b/d.txt" ) );
        Assert.assertNotNull( res );
        Assert.assertEquals( 2, res.getSize() );
    }

    @Test
    public void testGetResource()
    {
        FileResource res = this.fileService.getResource( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNull( res );

        Assert.assertTrue( this.fileService.createFile( new FileResourceName( "/a/b/c.txt" ), null ) );
        res = this.fileService.getResource( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNotNull( res );
    }

    @Test
    public void testGetResourceData()
    {
        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNull( data );

        Assert.assertTrue( this.fileService.createFile( new FileResourceName( "/a/b/c.txt" ), null ) );
        data = this.fileService.getResourceData( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( 0, data.getAsBytes().length );

        Assert.assertTrue( this.fileService.createFile( new FileResourceName( "/a/b/d.txt" ), FileResourceData.create( "hello" ) ) );
        data = this.fileService.getResourceData( new FileResourceName( "/a/b/d.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "hello", data.getAsString() );
        Assert.assertEquals( 5, data.getSize() );
    }

    @Test
    public void testSetResourceData()
    {
        Assert.assertFalse( this.fileService.setResourceData( new FileResourceName( "/a/b/c.txt" ), FileResourceData.create( "hello" ) ) );

        Assert.assertTrue( this.fileService.createFile( new FileResourceName( "/a/b/c.txt" ), null ) );
        Assert.assertTrue( this.fileService.setResourceData( new FileResourceName( "/a/b/c.txt" ), FileResourceData.create( "hello" ) ) );

        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/a/b/c.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "hello", data.getAsString() );
        Assert.assertEquals( 5, data.getSize() );
    }

    @Test
    public void testGetChildren()
    {
        createSampleData();

        List<FileResourceName> list = this.fileService.getChildren( new FileResourceName( "/some/unknown/path" ) );
        Assert.assertNotNull( list );
        Assert.assertEquals( 0, list.size() );

        list = this.fileService.getChildren( new FileResourceName( "/" ) );
        Assert.assertNotNull( list );
        Assert.assertEquals( 1, list.size() );
        Assert.assertEquals( list.get( 0 ).toString(), "/a" );

        list = this.fileService.getChildren( new FileResourceName( "/a/b/c" ) );
        Assert.assertNotNull( list );
        Assert.assertEquals( 2, list.size() );
        Assert.assertTrue( list.contains( new FileResourceName( "/a/b/c/other.txt" ) ) );
        Assert.assertTrue( list.contains( new FileResourceName( "/a/b/c/sample.txt" ) ) );

        list = this.fileService.getChildren( new FileResourceName( "/a/b/c/sample.txt" ) );
        Assert.assertNotNull( list );
        Assert.assertEquals( 0, list.size() );
    }

    @Test
    public void testDeleteResource()
    {
        createSampleData();

        Assert.assertFalse( this.fileService.deleteResource( new FileResourceName( "/some/unknown/path" ) ) );
        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/a/b/c" ) ).size() );

        Assert.assertTrue( this.fileService.deleteResource( new FileResourceName( "/a/b/c/sample.txt" ) ) );
        Assert.assertEquals( 1, this.fileService.getChildren( new FileResourceName( "/a/b/c" ) ).size() );

        Assert.assertTrue( this.fileService.deleteResource( new FileResourceName( "/a/b" ) ) );
        Assert.assertEquals( 0, this.fileService.getChildren( new FileResourceName( "/a/b" ) ).size() );

        Assert.assertTrue( this.fileService.deleteResource( new FileResourceName( "/" ) ) );
        Assert.assertEquals( 0, this.fileService.getChildren( new FileResourceName( "/" ) ).size() );
    }

    @Test
    public void testCopyResourceFile()
    {
        createSampleData();

        Assert.assertFalse( this.fileService.copyResource( new FileResourceName( "/some/unknown/path" ), new FileResourceName( "/a" ) ) );
        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/a/b/c" ) ).size() );

        Assert.assertFalse(
            this.fileService.copyResource( new FileResourceName( "/a/sample.txt" ), new FileResourceName( "/a/b/c/sample.txt" ) ) );

        Assert.assertTrue(
            this.fileService.copyResource( new FileResourceName( "/a/sample.txt" ), new FileResourceName( "/a/b/c/extra.txt" ) ) );
        Assert.assertEquals( 3, this.fileService.getChildren( new FileResourceName( "/a/b/c" ) ).size() );
        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/a/b/c/extra.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "/a/sample.txt", data.getAsString() );
    }

    @Test
    public void copyResourceFolder_non_existing_fromFolder()
    {
        createSampleData();
        Assert.assertFalse( this.fileService.copyResource( new FileResourceName( "/some/unknown/path" ), new FileResourceName( "/a" ) ) );
    }

    @Test
    public void copyResourceFolder_copy_self_to_sub()
    {
        createSampleData();
        Assert.assertTrue( this.fileService.copyResource( new FileResourceName( "/a" ), new FileResourceName( "/a/b/c" ) ) );

        final FileResourceData resourceData = this.fileService.getResourceData( new FileResourceName( "/a/b/c/b/c/other.txt" ) );
        Assert.assertNotNull( resourceData );
        Assert.assertEquals( "/a/b/c/other.txt", resourceData.getAsString() );
    }

    @Test
    public void copyResourceFolder_copy_sub_to_root()
    {
        createSampleData();
        Assert.assertTrue( this.fileService.copyResource( new FileResourceName( "/a/b" ), new FileResourceName( "/z" ) ) );

        Assert.assertEquals( "Should have 'a' and 'z' as children on root after copy", 2,
                             this.fileService.getChildren( new FileResourceName( "/" ) ).size() );

        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/z/c" ) ).size() );

        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/z/c/other.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "/a/b/c/other.txt", data.getAsString() );
    }

    @Test
    public void moveResourceFile_non_existing_from_file()
    {
        createSampleData();
        Assert.assertFalse( this.fileService.moveResource( new FileResourceName( "/some/unknown/path" ), new FileResourceName( "/a" ) ) );
    }

    @Test
    public void moveResourceFile_move_to_existing_sub_of_self()
    {
        createSampleData();

        fileService.createFolder( new FileResourceName( "/a/new" ) );

        Assert.assertTrue(
            this.fileService.moveResource( new FileResourceName( "/a/sample.txt" ), new FileResourceName( "/a/new/sample.txt" ) ) );

        fileService.createFolder( new FileResourceName( "/a/new/new2" ) );

        Assert.assertTrue( this.fileService.moveResource( new FileResourceName( "/a/new/sample.txt" ),
                                                          new FileResourceName( "/a/new/new2/sample.txt" ) ) );
    }

    @Test
    public void moveResourceFile_not_allowed_to_move_to_nonexisting_folder()
    {
        createSampleData();

        Assert.assertFalse(
            this.fileService.moveResource( new FileResourceName( "/a/sample.txt" ), new FileResourceName( "/a/new/sample.txt" ) ) );
    }

    @Test
    public void moveResourceFile_move_file_to_subfolder()
    {
        createSampleData();

        Assert.assertTrue(
            this.fileService.moveResource( new FileResourceName( "/a/sample.txt" ), new FileResourceName( "/a/b/c/extra.txt" ) ) );
        Assert.assertEquals( 1, this.fileService.getChildren( new FileResourceName( "/a" ) ).size() );
        Assert.assertEquals( 3, this.fileService.getChildren( new FileResourceName( "/a/b/c" ) ).size() );
        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/a/b/c/extra.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "/a/sample.txt", data.getAsString() );
    }

    @Test
    public void moveResourceFile_move_file_up()
    {
        createSampleData();

        this.fileService.createFile( new FileResourceName( "/a/b/c/d/sample.txt" ), FileResourceData.create( "/a/b/c/d/sample.txt" ) );

        Assert.assertTrue( this.fileService.moveResource( new FileResourceName( "a/b/c" ), new FileResourceName( "/a/c" ) ) );
        Assert.assertTrue( this.fileService.getResource( new FileResourceName( "/a/c/sample.txt" ) ) != null );
        Assert.assertTrue( this.fileService.getResource( new FileResourceName( "/a/c/d/sample.txt" ) ) != null );
        Assert.assertTrue( this.fileService.getResource( new FileResourceName( "/a/b/c/d/sample.txt" ) ) == null );
    }

    @Test
    public void moveResourceFile_move_to_self()
    {
        this.fileService.createFolder( new FileResourceName( "/d" ) );

        Assert.assertFalse( this.fileService.moveResource( new FileResourceName( "/d" ), new FileResourceName( "/d" ) ) );
    }

    @Test
    public void moveResourceFile_move_to_subfolder_of_self()
    {
        this.fileService.createFolder( new FileResourceName( "/d" ) );

        Assert.assertFalse( this.fileService.moveResource( new FileResourceName( "/d" ), new FileResourceName( "/d/e" ) ) );
    }

    @Test
    public void moveResourceFolder_non_existing_folder()
    {
        createSampleData();
        Assert.assertFalse( this.fileService.moveResource( new FileResourceName( "/some/unknown/path" ), new FileResourceName( "/a" ) ) );
    }

    @Test
    public void moveResourceFolder_move_to_sub_subfolder_of_self()
    {
        createSampleData();
        Assert.assertFalse( this.fileService.moveResource( new FileResourceName( "/a" ), new FileResourceName( "/a/b/c" ) ) );
    }

    @Test
    public void moveResourceFolder_move_to_new_name_same_level()
    {
        createSampleData();

        Assert.assertTrue( this.fileService.moveResource( new FileResourceName( "/a/b" ), new FileResourceName( "/z" ) ) );
        Assert.assertEquals( 1, this.fileService.getChildren( new FileResourceName( "/a" ) ).size() );
        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/" ) ).size() );
        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/z/c" ) ).size() );
        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/z/c/other.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "/a/b/c/other.txt", data.getAsString() );
    }


    @Test
    public void moveResourceFolder_move_to_new_name_on_level_down()
    {
        createSampleData();

        this.fileService.createFolder( new FileResourceName( "/z" ) );

        Assert.assertTrue( this.fileService.moveResource( new FileResourceName( "/a/b" ), new FileResourceName( "/z/x" ) ) );
        Assert.assertEquals( 1, this.fileService.getChildren( new FileResourceName( "/a" ) ).size() );
        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/" ) ).size() );
        Assert.assertEquals( 2, this.fileService.getChildren( new FileResourceName( "/z/x/c" ) ).size() );
        FileResourceData data = this.fileService.getResourceData( new FileResourceName( "/z/x/c/other.txt" ) );
        Assert.assertNotNull( data );
        Assert.assertEquals( "/a/b/c/other.txt", data.getAsString() );
    }

    private void createSampleData()
    {
        this.fileService.createFile( new FileResourceName( "/a/sample.txt" ), FileResourceData.create( "/a/sample.txt" ) );
        this.fileService.createFile( new FileResourceName( "/a/b/sample.txt" ), FileResourceData.create( "/a/b/sample.txt" ) );
        this.fileService.createFile( new FileResourceName( "/a/b/c/sample.txt" ), FileResourceData.create( "/a/b/c/sample.txt" ) );
        this.fileService.createFile( new FileResourceName( "/a/b/c/other.txt" ), FileResourceData.create( "/a/b/c/other.txt" ) );
    }
}
