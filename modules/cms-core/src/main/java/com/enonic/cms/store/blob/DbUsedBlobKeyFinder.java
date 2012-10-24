/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.blob;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.gc.UsedBlobKeyFinder;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.store.dao.BinaryDataDao;

@Component("usedBlobStoreFinder")
public final class DbUsedBlobKeyFinder
    implements UsedBlobKeyFinder
{
    private BinaryDataDao binaryDataDao;

    public Set<BlobKey> findKeys()
        throws Exception
    {
        final HashSet<BlobKey> keys = Sets.newHashSet();

        findFromBinaryData( keys );

        return keys;
    }

    private void findFromBinaryData( final Set<BlobKey> keys )
    {
        for ( final BinaryDataEntity entity : this.binaryDataDao.findAll() )
        {
            final String key = entity.getBlobKey();
            if ( key != null )
            {
                keys.add( new BlobKey( key ) );
            }
        }
    }

    @Autowired
    public void setBinaryDataDao( final BinaryDataDao dao )
    {
        this.binaryDataDao = dao;
    }
}
