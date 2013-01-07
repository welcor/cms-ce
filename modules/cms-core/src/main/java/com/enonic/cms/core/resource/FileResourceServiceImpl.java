package com.enonic.cms.core.resource;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import com.enonic.cms.framework.util.MimeTypeResolver;

@Service("fileResourceService")
public class FileResourceServiceImpl
    implements FileResourceService
{
    private File resourceRoot;

    private MimeTypeResolver mimeTypeResolver;

    @Override
    public FileResource getResource( final FileResourceName name )
    {
        final File entity = getFile( name );

        if ( !entity.exists() && name.isRoot() )
        {
            createFolder( name );

            return newResource( name, resourceRoot );
        }

        if ( entity.exists() )
        {
            return newResource( name, entity );
        }

        return null;
    }

    private boolean doCreateFolder( FileResourceName name )
    {
        if ( name == null )
        {
            return false;
        }

        File newFolder = getFile( name );

        if ( newFolder.exists() )
        {
            return false;
        }

        return newFolder.mkdirs();
    }

    private FileResource newResource( FileResourceName name, File file )
    {
        FileResource res = new FileResource( name );
        res.setFolder( file.isDirectory() );
        res.setSize( file.length() );
        res.setMimeType( mimeTypeResolver.getMimeType( name.getName() ) );
        res.setLastModified( new DateTime( file.lastModified() ) );
        return res;
    }

    @Override
    public boolean createFolder( final FileResourceName name )
    {
        return doCreateFolder( name );
    }

    @Override
    public boolean createFile( FileResourceName name, FileResourceData data )
    {
        return !name.isRoot() && doCreateFile( name, data );
    }

    private boolean doCreateFile( FileResourceName name, FileResourceData data )
    {
        final File newFile = getFile( name );

        if ( newFile.exists() )
        {
            return false;
        }

        try
        {
            Files.createParentDirs( newFile );

            if ( data != null )
            {
                Files.write( data.getAsBytes(), newFile );
            }
            else
            {
                Files.touch( newFile );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to create file " + newFile.getAbsolutePath(), e );
        }

        return true;
    }

    @Override
    public boolean deleteResource( final FileResourceName name )
    {
        File fileToDelete = getFile( name );

        if ( fileToDelete.exists() )
        {
            if ( fileToDelete.isDirectory() )
            {
                try
                {
                    FileUtils.deleteDirectory( fileToDelete );
                    return true;
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( "Not able to delete folder: " + fileToDelete.getAbsolutePath(), e );
                }
            }
            else
            {
                return fileToDelete.delete();
            }
        }

        return false;
    }

    @Override
    public List<FileResourceName> getChildren( final FileResourceName name )
    {
        final ArrayList<FileResourceName> list = new ArrayList<FileResourceName>();

        final File parent = getFile( name );

        if ( parent.exists() && parent.isDirectory() )
        {
            final File[] files = parent.listFiles();

            // sort files, because listFiles does not guaranty any order.
            Arrays.sort( files );

            for ( final File file : files )
            {
                list.add( new FileResourceName( name, file.getName() ) );
            }
        }

        return list;
    }

    @Override
    public FileResourceData getResourceData( final FileResourceName name )
    {
        File file = getFile( name );

        if ( !file.exists() )
        {
            return null;
        }

        if ( file.isDirectory() )
        {
            return null;
        }

        try
        {
            final byte[] bytes = Files.toByteArray( file );
            final FileResourceData data = new FileResourceData();
            data.setAsBytes( bytes );
            return data;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to read file: " + file.getAbsolutePath(), e );
        }
    }

    private File getFile( final FileResourceName name )
    {
        if ( name.isRoot() )
        {
            return resourceRoot;
        }

        return new File( resourceRoot, name.getPath() );
    }

    @Override
    public boolean setResourceData( final FileResourceName name, final FileResourceData data )
    {
        File file = getFile( name );

        if ( !file.exists() )
        {
            return false;
        }

        if ( file.isDirectory() )
        {
            return false;
        }

        try
        {
            Files.write( data.getAsBytes(), file );
            return true;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to write to file " + file.getAbsolutePath(), e );
        }
    }

    @Override
    public boolean moveResource( final FileResourceName from, final FileResourceName to )
    {
        File fromFile = getFile( from );

        if ( !fromFile.exists() )
        {
            return false;
        }

        File toFile = getFile( to );

        if ( toFile.exists() )
        {
            return false;
        }

        if ( fromFile.isDirectory() )
        {
            return doMoveDirectory( fromFile, toFile );
        }

        if ( fromFile.isFile() )
        {
            return doMoveFile( fromFile, toFile );

        }

        return false;
    }

    private boolean doMoveFile( final File fromFile, final File toFile )
    {
        final File parent = toFile.getParentFile();

        if ( !parent.exists() )
        {
            return false;
        }

        try
        {
            Files.move( fromFile, toFile );
            return true;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to move file " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath(), e );
        }
    }

    private boolean doMoveDirectory( final File fromFile, final File toFile )
    {
        if ( isSubFolderOf( fromFile, toFile ) )
        {
            return false;
        }

        try
        {
            FileUtils.moveDirectory( fromFile, toFile );
            return true;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to move folder " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath(), e );
        }
    }

    private boolean isSubFolderOf( final File fromFile, final File toFile )
    {
        File parent = toFile.getParentFile();

        while ( parent != null )
        {
            if ( parent.equals( resourceRoot ) )
            {
                return false;
            }

            if ( parent.equals( fromFile ) )
            {
                return true;
            }

            parent = parent.getParentFile();
        }

        return false;
    }

    @Override
    public boolean copyResource( final FileResourceName from, final FileResourceName to )
    {
        File fromFile = getFile( from );

        if ( !fromFile.exists() )
        {
            return false;
        }

        if ( fromFile.isFile() )
        {
            return doCopyResourceFile( fromFile, to );
        }

        if ( fromFile.isDirectory() )
        {
            return doCopyResourceFolder( to, fromFile );
        }

        return false;
    }

    private boolean doCopyResourceFolder( final FileResourceName to, final File fromFile )
    {
        File toFile = getFile( to );

        try
        {
            FileUtils.copyDirectory( fromFile, toFile );
            return true;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to copy directory " + fromFile + " to " + toFile, e );
        }
    }

    private boolean doCopyResourceFile( File fromFile, FileResourceName to )
    {
        File toFile = getFile( to );

        if ( toFile.exists() )
        {
            return false;
        }

        try
        {
            Files.copy( fromFile, toFile );
            return true;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Not able to copy from file " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath(), e );
        }
    }

    @Override
    public InputStream getResourceStream( final FileResourceName name, final boolean ignoreBom )
    {
        final File file = getFile( name );

        if ( !file.isFile() )
        {
            return null;
        }

        try
        {
            final FileInputStream in = new FileInputStream( file );
            if (ignoreBom) {
                return new BOMInputStream( in, false );
            } else {
                return in;
            }
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( "Failed to open " + name.getPath() );
        }
    }

    @Value("${cms.resource.path}")
    public void setResourceRoot( final File resourceRoot )
    {
        this.resourceRoot = resourceRoot;
    }

    @Autowired
    public void setMimeTypeResolver( final MimeTypeResolver mimeTypeResolver )
    {
        this.mimeTypeResolver = mimeTypeResolver;
    }
}
