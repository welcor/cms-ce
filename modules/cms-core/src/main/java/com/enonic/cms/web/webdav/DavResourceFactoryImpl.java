/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import java.io.File;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;

import com.enonic.cms.framework.util.MimeTypeResolver;

final class DavResourceFactoryImpl
    implements DavResourceFactory
{
    private final File resourceRoot;

    private final LockManager lockManager;

    private final MimeTypeResolver mimeTypeResolver;

    public DavResourceFactoryImpl( final DavConfiguration configuration )
    {
        this.mimeTypeResolver = configuration.getMimeTypeResolver();
        this.resourceRoot = configuration.getResourceRoot();
        this.lockManager = new SimpleLockManager();
    }

    @Override
    public DavResource createResource( final DavResourceLocator locator, final DavSession session )
        throws DavException
    {
        final File file = new File( this.resourceRoot, locator.getResourcePath() );
        final DavResourceImpl resource = new DavResourceImpl( file, locator, session, this, this.mimeTypeResolver );
        resource.addLockManager( this.lockManager );
        return resource;
    }

    @Override
    public DavResource createResource( final DavResourceLocator locator, final DavServletRequest req, final DavServletResponse res )
        throws DavException
    {
        return createResource( locator, req.getDavSession() );
    }
}
