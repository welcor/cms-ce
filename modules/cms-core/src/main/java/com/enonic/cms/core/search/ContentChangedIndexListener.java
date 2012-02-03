/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.store.support.EntityChangeListener;
import com.enonic.cms.store.support.EntityPostCommitListenerHub;

@Component
public class ContentChangedIndexListener
    implements EntityChangeListener
{
    private Logger LOG = Logger.getLogger( ContentChangedIndexListener.class.getName() );

    @Autowired
    private ContentIndexService contentIndexService;

    public ContentChangedIndexListener()
    {
    }

    @PostConstruct
    public void registerContentChangedListener()
    {
        EntityPostCommitListenerHub.getInstance().addListener( this, ContentEntity.class );
    }

    public void entityInserted( Session session, Object entity )
    {
        final ContentEntity content = (ContentEntity) entity;
        LOG.info( "Content inserted, id=" + content.getKey() );

        indexContent( content );
    }

    public void entityUpdated( Session session, Object entity )
    {
        final ContentEntity content = (ContentEntity) entity;
        if ( content.isDeleted() )
        {
            entityDeleted( session, entity );
            return;
        }

        LOG.info( "Content updated, id=" + content.getKey() );
        indexContent( content );
    }

    public void entityDeleted( Session session, Object entity )
    {
        final ContentEntity content = (ContentEntity) entity;
        LOG.info( "Content deleted, id=" + content.getKey() );

        deleteContent( content );
    }

    private void indexContent( ContentEntity content )
    {
        ContentDocument contentDoc = new ContentDocument( content.getKey() );
//        contentIndexService.index( contentDoc, false );

        contentIndexService.flush();
    }

    private void deleteContent( ContentEntity content )
    {
//        contentIndexService.remove( content.getKey() );

        contentIndexService.flush();
    }
}
