/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ResourceType;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

final class DavResourceImpl
    implements DavResource
{
    private final DavSession session;

    private final DavResourceFactory factory;

    private final DavResourceLocator locator;

    private final File file;

    private DavPropertySet properties;

    private LockManager lockManager;

    private final DavConfiguration configuration;

    public DavResourceImpl( final File file, final DavResourceLocator locator, final DavSession session, final DavResourceFactory factory,
                            final DavConfiguration configuration )
    {
        this.file = file;
        this.locator = locator;
        this.session = session;
        this.factory = factory;
        this.configuration = configuration;
    }

    @Override
    public String getComplianceClass()
    {
        return "1, 2";
    }

    @Override
    public String getSupportedMethods()
    {
        return METHODS;
    }

    @Override
    public boolean exists()
    {
        return ( this.file != null ) && this.file.exists() && !isHidden( this.file.getName() );
    }

    @Override
    public boolean isCollection()
    {
        return this.file.isDirectory();
    }

    @Override
    public String getDisplayName()
    {
        final String resPath = getResourcePath();
        return ( resPath != null ) ? Text.getName( resPath ) : resPath;
    }

    @Override
    public DavResourceLocator getLocator()
    {
        return this.locator;
    }

    @Override
    public String getResourcePath()
    {
        return this.locator.getResourcePath();
    }

    @Override
    public String getHref()
    {
        return this.locator.getHref( isCollection() );
    }

    @Override
    public long getModificationTime()
    {
        return this.file.lastModified();
    }

    @Override
    public void spool( final OutputContext out )
        throws IOException
    {
        if ( isCollection() )
        {
            spoolCollection( out );
        }
        else
        {
            spoolResource( out );
        }
    }

    private void spoolCollection( final OutputContext out )
        throws IOException
    {
        new DavFolderIndexWriter( this ).write( out );
    }

    private void spoolResource( final OutputContext out )
        throws IOException
    {
        out.setContentLength( this.file.length() );
        out.setModificationTime( this.file.lastModified() );
        out.setContentType( this.configuration.getMimeTypeResolver().getMimeType( this.file.getName() ) );

        if ( out.hasStream() )
        {
            Files.copy( this.file, out.getOutputStream() );
        }
    }

    @Override
    public DavPropertyName[] getPropertyNames()
    {
        return getProperties().getPropertyNames();
    }

    @Override
    public DavProperty<?> getProperty( final DavPropertyName name )
    {
        return getProperties().get( name );
    }

    @Override
    public DavPropertySet getProperties()
    {
        if ( !exists() )
        {
            this.properties = new DavPropertySet();
        }

        if ( this.properties == null )
        {
            this.properties = createProperties();
        }

        return this.properties;
    }

    @Override
    public void setProperty( final DavProperty<?> property )
        throws DavException
    {
        // Do nothing
    }

    @Override
    public void removeProperty( final DavPropertyName name )
        throws DavException
    {
        // Do nothing
    }

    @Override
    public MultiStatusResponse alterProperties( final List<? extends PropEntry> entries )
        throws DavException
    {
        return null;
    }

    @Override
    public DavResource getCollection()
    {
        if ( getResourcePath() == null )
        {
            return null;
        }

        if ( getResourcePath().equals( "/" ) )
        {
            return null;
        }

        String parentPath = Text.getRelativeParent( getResourcePath(), 1 );
        if ( parentPath.equals( "" ) )
        {
            parentPath = "/";
        }

        final DavResourceLocator parent = createRelativeLocator( parentPath );
        return createResource( parent );
    }

    @Override
    public void addMember( final DavResource member, InputContext in )
        throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_CONFLICT );
        }

        if ( !isCollection() )
        {
            throw new DavException( HttpServletResponse.SC_BAD_REQUEST );
        }

        final String memberName = Text.getName( member.getLocator().getRepositoryPath() );
        final File localFile = new File( this.file, memberName );

        if ( in.hasStream() )
        {
            createFile( localFile, in );
        }
        else
        {
            createCollection( localFile, in );
        }
    }

    private void createCollection( final File localFile, final InputContext in )
        throws DavException
    {
        if ( in.hasStream() )
        {
            throw new DavException( DavServletResponse.SC_UNSUPPORTED_MEDIA_TYPE );
        }

        if ( !localFile.mkdirs() )
        {
            throw new DavException( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create directory" );
        }
    }

    private void createFile( final File localFile, final InputContext in )
        throws DavException
    {
        try
        {
            ByteStreams.copy( in.getInputStream(), Files.newOutputStreamSupplier( localFile ) );
        }
        catch ( IOException e )
        {
            throw new DavException( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e );
        }
    }

    private DavResourceLocator createRelativeLocator( final String path )
    {
        return this.locator.getFactory().createResourceLocator( this.locator.getPrefix(), this.locator.getWorkspacePath(), path, false );
    }

    private DavResource createResource( final DavResourceLocator locator )
    {
        try
        {
            return this.factory.createResource( locator, this.session );
        }
        catch ( final DavException e )
        {
            return null;
        }
    }

    private boolean isHidden( final String name )
    {
        return this.configuration.isHidden( name );
    }

    @Override
    public DavResourceIterator getMembers()
    {
        final List<DavResource> list = Lists.newArrayList();

        if ( !exists() )
        {
            return new DavResourceIteratorImpl( list );
        }

        if ( !isCollection() )
        {
            return new DavResourceIteratorImpl( list );
        }

        for ( final String item : this.file.list() )
        {
            if ( !isHidden( item ) )
            {
                String path = this.locator.getResourcePath();
                if ( !path.endsWith( "/" ) )
                {
                    path += '/';
                }
                path += item;
                final DavResourceLocator resourceLocator = createRelativeLocator( path );
                final DavResource resource = createResource( resourceLocator );

                if ( resource != null )
                {
                    list.add( resource );
                }
            }
        }

        return new DavResourceIteratorImpl( list );
    }

    @Override
    public void removeMember( final DavResource member )
        throws DavException
    {
        final File targetFile = ( (DavResourceImpl) member ).file;
        if ( !targetFile.exists() )
        {
            throw new DavException( HttpServletResponse.SC_NOT_FOUND );
        }

        if ( !FileUtils.deleteQuietly( targetFile ) )
        {
            final String type = targetFile.isDirectory() ? "directory" : "file";
            throw new DavException( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not remove " + type );
        }
    }

    @Override
    public void move( final DavResource target )
        throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        final File targetFile = ( (DavResourceImpl) target ).file;

        try
        {
            if ( isCollection() )
            {
                FileUtils.moveDirectory( this.file, targetFile );
            }
            else
            {
                FileUtils.moveFile( this.file, targetFile );
            }
        }
        catch ( final IOException e )
        {
            throw new DavException( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e );
        }
    }

    @Override
    public void copy( final DavResource target, final boolean shallow )
        throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        if ( !target.getCollection().exists() )
        {
            throw new DavException( DavServletResponse.SC_CONFLICT );
        }

        final File targetFile = ( (DavResourceImpl) target ).file;

        try
        {
            if ( isCollection() )
            {
                FileUtils.copyDirectory( this.file, targetFile );
            }
            else
            {
                FileUtils.copyFile( this.file, targetFile );
            }
        }
        catch ( final IOException e )
        {
            throw new DavException( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e );
        }
    }

    @Override
    public boolean isLockable( final Type type, final Scope scope )
    {
        return Type.WRITE.equals( type ) && Scope.EXCLUSIVE.equals( scope );
    }

    @Override
    public boolean hasLock( final Type type, final Scope scope )
    {
        return getLock( type, scope ) != null;
    }

    @Override
    public ActiveLock getLock( final Type type, final Scope scope )
    {
        if ( exists() && Type.WRITE.equals( type ) && Scope.EXCLUSIVE.equals( scope ) )
        {
            return this.lockManager.getLock( type, scope, this );
        }

        return null;
    }

    @Override
    public ActiveLock[] getLocks()
    {
        final ActiveLock writeLock = getLock( Type.WRITE, Scope.EXCLUSIVE );
        return ( writeLock != null ) ? new ActiveLock[]{writeLock} : new ActiveLock[0];
    }

    @Override
    public ActiveLock lock( final LockInfo info )
        throws DavException
    {
        if ( isLockable( info.getType(), info.getScope() ) )
        {
            return this.lockManager.createLock( info, this );
        }

        throw new DavException( DavServletResponse.SC_PRECONDITION_FAILED );
    }

    @Override
    public ActiveLock refreshLock( final LockInfo info, final String token )
        throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        final ActiveLock lock = getLock( info.getType(), info.getScope() );
        if ( lock == null )
        {
            throw new DavException( DavServletResponse.SC_PRECONDITION_FAILED );
        }

        return this.lockManager.refreshLock( info, token, this );
    }

    @Override
    public void unlock( final String token )
        throws DavException
    {
        final ActiveLock lock = getLock( Type.WRITE, Scope.EXCLUSIVE );
        if ( lock == null )
        {
            throw new DavException( DavServletResponse.SC_PRECONDITION_FAILED );
        }

        if ( lock.isLockedByToken( token ) )
        {
            this.lockManager.releaseLock( token, this );
        }
        else
        {
            throw new DavException( DavServletResponse.SC_LOCKED );
        }
    }

    @Override
    public void addLockManager( final LockManager lockManager )
    {
        this.lockManager = lockManager;
    }

    @Override
    public DavResourceFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public DavSession getSession()
    {
        return this.session;
    }

    private DavPropertySet createProperties()
    {
        final DavPropertySet result = new DavPropertySet();

        if ( getDisplayName() != null )
        {
            result.add( new DefaultDavProperty<String>( DavPropertyName.DISPLAYNAME, getDisplayName() ) );
        }

        if ( isCollection() )
        {
            result.add( new ResourceType( ResourceType.COLLECTION ) );
            result.add( new DefaultDavProperty<String>( DavPropertyName.ISCOLLECTION, "1" ) );
        }
        else
        {
            result.add( new ResourceType( ResourceType.DEFAULT_RESOURCE ) );
            result.add( new DefaultDavProperty<String>( DavPropertyName.ISCOLLECTION, "0" ) );
        }

        final long modifiedTime = this.file.lastModified();
        if ( modifiedTime != DavConstants.UNDEFINED_TIME )
        {
            result.add( new DefaultDavProperty<String>( DavPropertyName.GETLASTMODIFIED,
                                                        DavConstants.modificationDateFormat.format( new Date( modifiedTime ) ) ) );
        }

        if ( !isCollection() )
        {
            result.add( new DefaultDavProperty<String>( DavPropertyName.GETCONTENTLENGTH, String.valueOf( this.file.length() ) ) );
        }

        applyLocking( result );

        return result;
    }

    private void applyLocking( final DavPropertySet result )
    {
        result.add( new LockDiscovery( getLock( Type.WRITE, Scope.EXCLUSIVE ) ) );
        SupportedLock supportedLock = new SupportedLock();
        supportedLock.addEntry( Type.WRITE, Scope.EXCLUSIVE );
        result.add( supportedLock );
    }
}
