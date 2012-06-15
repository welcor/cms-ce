/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.memory.MemoryBlobRecord;
import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceData;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.store.support.EntityChangeListener;
import com.enonic.cms.store.support.EntityChangeListenerHub;
import com.enonic.cms.store.vfs.db.VirtualFileEntity;

@Service("fileResourceService")
public final class FileResourceServiceImpl
    implements FileResourceService, EntityChangeListener
{
    private BlobStore blobStore;

    private SessionFactory sessionFactory;

    private MimeTypeResolver mimeTypeResolver;

    private List<FileResourceListener> listeners;

    public FileResourceServiceImpl()
    {
        EntityChangeListenerHub.getInstance().addListener( this );
    }

    @Autowired
    public void setBlobStore( BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Autowired
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    @Autowired
    public void setMimeTypeResolver( final MimeTypeResolver mimeTypeResolver )
    {
        this.mimeTypeResolver = mimeTypeResolver;
    }

    private Session openSession()
    {
        return this.sessionFactory.getCurrentSession();
    }

    private FileResource newResource( FileResourceName name, VirtualFileEntity entity )
    {
        FileResource res = new FileResource( name );
        res.setFolder( entity.isFolder() );
        res.setSize( entity.getLength() );
        res.setBlobKey( entity.getBlobKey() );
        res.setMimeType( mimeTypeResolver.getMimeType( name.getName() ) );
        res.setLastModified( new DateTime( entity.getLastModified() ) );
        return res;
    }

    public FileResource getResource( FileResourceName name )
    {
        Session session = openSession();
        return doGetResource( session, name );
    }

    private FileResource doGetResource( Session session, FileResourceName name )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );
        if ( entity == null )
        {
            if ( name.isRoot() )
            {
                doCreateFolder( session, name );
                entity = findEntity( session, key );
            }
        }

        if ( entity != null )
        {
            return newResource( name, entity );
        }

        return null;
    }

    private boolean doCreateFolder( Session session, FileResourceName name )
    {
        if ( name == null )
        {
            return false;
        }

        doCreateFolder( session, name.getParent() );

        String key = createKey( name );
        VirtualFileEntity newVirtualFile = findEntity( session, key );
        if ( newVirtualFile != null )
        {
            return false;
        }

        newVirtualFile = createVirtualFileEntity( key, name, true );

        String parentKey = createKey( name.getParent() );
        if ( parentKey != null )
        {
            VirtualFileEntity parent = findEntity( session, parentKey );
            parent.addChild( newVirtualFile );
            session.update( parent );
        }

        session.save( newVirtualFile );

        return true;
    }

    public boolean createFolder( FileResourceName name )
    {
        Session session = openSession();
        return doCreateFolder( session, name );
    }

    public boolean createFile( FileResourceName name, FileResourceData data )
    {
        Session session = openSession();
        return !name.isRoot() && doCreateFile( session, name, data );
    }

    private boolean doCreateFile( Session session, FileResourceName name, FileResourceData data )
    {
        String key = createKey( name );
        if ( findEntity( session, key ) != null )
        {
            return false;
        }

        doCreateFolder( session, name.getParent() );

        VirtualFileEntity newVirtualFile = createVirtualFileEntity( key, name, false );
        setBlob( session, newVirtualFile, data != null ? data.getAsBytes() : new byte[0] );

        String parentKey = createKey( name.getParent() );
        if ( parentKey != null )
        {
            VirtualFileEntity parent = findEntity( session, parentKey );
            parent.addChild( newVirtualFile );
            session.saveOrUpdate( parent );
        }

        session.saveOrUpdate( newVirtualFile );

        return true;
    }

    private VirtualFileEntity createVirtualFileEntity( String key, FileResourceName name, boolean isFolder )
    {
        VirtualFileEntity virtualFile = new VirtualFileEntity();
        virtualFile.setKey( key );
        virtualFile.setBlobKey( null );
        virtualFile.setLength( isFolder ? -1 : 0 );
        virtualFile.setName( name.getName() );
        virtualFile.setLastModified( System.currentTimeMillis() );

        return virtualFile;
    }

    public boolean deleteResource( FileResourceName name )
    {
        Session session = openSession();
        return doDeleteResource( session, name );
    }

    private boolean doDeleteResource( Session session, FileResourceName name )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );
        return doDeleteResource( session, entity );
    }

    private boolean doDeleteResource( Session session, VirtualFileEntity entity )
    {
        if ( entity == null )
        {
            return false;
        }

        VirtualFileEntity parent = entity.getParent();
        if ( parent != null )
        {
            parent.removeChild( entity );
            session.update( parent );
        }

        entity.setParent( null );
        session.delete( entity );

        return true;
    }

    public List<FileResourceName> getChildren( FileResourceName name )
    {
        Session session = openSession();
        return doGetChildren( session, name );
    }

    private List<FileResourceName> doGetChildren( Session session, FileResourceName name )
    {
        ArrayList<FileResourceName> list = new ArrayList<FileResourceName>();

        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );

        if ( entity != null )
        {
            for ( VirtualFileEntity child : entity.getChildren() )
            {
                list.add( new FileResourceName( name, child.getName() ) );
            }
        }

        return list;
    }

    public FileResourceData getResourceData( FileResourceName name )
    {
        Session session = openSession();
        return doGetResourceData( session, name );
    }

    private FileResourceData doGetResourceData( Session session, FileResourceName name )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );

        if ( entity == null )
        {
            return null;
        }

        if ( entity.isFolder() )
        {
            return null;
        }

        final byte[] bytes = getBlob( entity );
        if ( bytes == null )
        {
            throw new IllegalStateException( "Blob for resource [" + name.toString() +
                                                 "] is not found. Please check your blobstore configuration." );
        }

        final FileResourceData data = new FileResourceData();
        data.setAsBytes( bytes );
        return data;
    }

    public boolean setResourceData( FileResourceName name, FileResourceData data )
    {
        Session session = openSession();
        return doSetResourceData( session, name, data );
    }

    private boolean doSetResourceData( Session session, FileResourceName name, FileResourceData data )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );

        if ( entity == null )
        {
            return false;
        }

        if ( entity.isFolder() )
        {
            return false;
        }

        setBlob( session, entity, data.getAsBytes() );

        return true;
    }

    private void setBlob( Session session, VirtualFileEntity entity, byte[] data )
    {
        BlobRecord blob = new MemoryBlobRecord( data );
        this.blobStore.addRecord( blob.getStream() );
        entity.setBlobKey( blob.getKey().toString() );
        entity.setLength( blob.getLength() );
        entity.setLastModified( System.currentTimeMillis() );

        VirtualFileEntity parent = getRoot( entity );
        session.saveOrUpdate( parent );
    }

    private VirtualFileEntity getRoot( VirtualFileEntity entity )
    {
        VirtualFileEntity parent = entity;

        while ( parent.getParent() != null )
        {
            parent = parent.getParent();
        }

        return parent;
    }

    private byte[] getBlob( VirtualFileEntity entity )
    {
        String key = entity.getBlobKey();
        if ( key == null )
        {
            return null;
        }

        BlobRecord blob = this.blobStore.getRecord( new BlobKey( key ) );
        return blob != null ? blob.getAsBytes() : null;
    }

    private String createKey( FileResourceName name )
    {
        if ( name == null )
        {
            return null;
        }

        try
        {
            return DigestUtils.shaHex( name.getPath().getBytes( "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    private VirtualFileEntity findEntity( Session session, String key )
    {
        return (VirtualFileEntity) session.get( VirtualFileEntity.class, key );
    }

    public boolean moveResource( FileResourceName from, FileResourceName to )
    {
        Session session = openSession();
        return doMoveResource( session, from, to );
    }

    private boolean doMoveResource( Session session, FileResourceName from, FileResourceName to )
    {
        if ( doCopyResource( session, from, to ) )
        {
            doDeleteResource( session, from );
            return true;
        }

        return false;
    }

    public boolean copyResource( FileResourceName from, FileResourceName to )
    {
        Session session = openSession();
        return doCopyResource( session, from, to );
    }

    private boolean doCopyResource( Session session, FileResourceName from, FileResourceName to )
    {
        String fromKey = createKey( from );
        VirtualFileEntity fromEntity = findEntity( session, fromKey );
        return doCopyResource( session, fromEntity, to );
    }

    private boolean doCopyResource( Session session, VirtualFileEntity from, FileResourceName to )
    {
        if ( from == null )
        {
            return false;
        }

        if ( from.isFile() )
        {
            return doCopyResourceFile( session, from, to );
        }
        else
        {
            return doCopyResourceFolder( session, from, to );
        }
    }

    private boolean doCopyResourceFile( Session session, VirtualFileEntity from, FileResourceName to )
    {
        String toKey = createKey( to );
        VirtualFileEntity toEntity = findEntity( session, toKey );

        if ( toEntity != null )
        {
            return false;
        }

        doCreateFolder( session, to.getParent() );
        toEntity = createNewEntity( session, from, to );
        session.saveOrUpdate( toEntity );
        return true;
    }

    private boolean doCopyResourceFolder( Session session, VirtualFileEntity from, FileResourceName to )
    {
        if ( moveToSubfolderOfSelf( from, to ) )
        {
            return false;
        }

        String toKey = createKey( to );
        VirtualFileEntity toEntity = findEntity( session, toKey );

        if ( toEntity != null )
        {
            return false;
        }

        doCreateFolder( session, to );

        String fromKey = from.getKey();
        VirtualFileEntity parent = findEntity( session, fromKey );

        for ( VirtualFileEntity child : parent.getChildren() )
        {
            doCopyResource( session, child, new FileResourceName( to, child.getName() ) );
        }

        return true;
    }

    private boolean moveToSubfolderOfSelf( VirtualFileEntity parent, FileResourceName potentialChild )
    {
        FileResourceName parentFileResource = new FileResourceName( parent.getName() );

        if ( getAllParents( potentialChild ).contains( parentFileResource ) )
        {
            return true;
        }

        return false;
    }

    private List<FileResourceName> getAllParents( FileResourceName fileResourceName )
    {
        List<FileResourceName> allParents = new ArrayList<FileResourceName>();

        FileResourceName currParent = fileResourceName.getParent();

        while ( currParent != null )
        {
            allParents.add( currParent );
            currParent = currParent.getParent();
        }

        return allParents;
    }

    private VirtualFileEntity createNewEntity( Session session, VirtualFileEntity oldEntity, FileResourceName newName )
    {
        VirtualFileEntity newVirtualFile = new VirtualFileEntity();
        newVirtualFile.setKey( createKey( newName ) );
        newVirtualFile.setLastModified( System.currentTimeMillis() );
        newVirtualFile.setLength( oldEntity.getLength() );
        newVirtualFile.setBlobKey( oldEntity.getBlobKey() );
        newVirtualFile.setName( newName.getName() );

        String parentKey = createKey( newName.getParent() );

        if ( parentKey != null )
        {
            VirtualFileEntity parent = findEntity( session, parentKey );
            parent.addChild( newVirtualFile );
        }

        return newVirtualFile;
    }

    private FileResourceName createNameFromEntity( VirtualFileEntity entity )
    {
        if ( entity.getParent() == null )
        {
            return new FileResourceName( "/" );
        }

        final VirtualFileEntity parent = entity.getParent();
        if ( parent == null )
        {
            return new FileResourceName( entity.getName() );
        }
        else
        {
            return new FileResourceName( createNameFromEntity( parent ), entity.getName() );
        }
    }

    private void publishResourceEvent( VirtualFileEntity entity, FileResourceEvent.Type type )
    {
        if ( this.listeners.isEmpty() )
        {
            return;
        }

        final FileResourceName name = createNameFromEntity( entity );
        final FileResourceEvent event = new FileResourceEvent( type, name );

        for ( FileResourceListener listener : this.listeners )
        {
            listener.resourceChanged( event );
        }
    }

    public void entityInserted( Session session, Object entity )
    {
        if ( entity instanceof VirtualFileEntity )
        {
            publishResourceEvent( (VirtualFileEntity) entity, FileResourceEvent.Type.ADDED );
        }
    }

    public void entityUpdated( Session session, Object entity )
    {
        if ( entity instanceof VirtualFileEntity )
        {
            publishResourceEvent( (VirtualFileEntity) entity, FileResourceEvent.Type.UPDATED );
        }
    }

    public void entityDeleted( Session session, Object entity )
    {
        if ( entity instanceof VirtualFileEntity )
        {
            publishResourceEvent( (VirtualFileEntity) entity, FileResourceEvent.Type.DELETED );
        }
    }

    @Autowired(required = false)
    public void setListeners( final List<FileResourceListener> listeners )
    {
        this.listeners = listeners;
    }
}
