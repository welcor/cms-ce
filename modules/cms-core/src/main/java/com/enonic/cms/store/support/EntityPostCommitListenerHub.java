/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.util.List;

import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

@Component
public final class EntityPostCommitListenerHub
    implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( EntityPostCommitListenerHub.class );

    private final static EntityPostCommitListenerHub INSTANCE = new EntityPostCommitListenerHub();


    private final ListMultimap<Class, EntityChangeListener> listeners;

    private EntityPostCommitListenerHub()
    {
        this.listeners = Multimaps.synchronizedListMultimap( ArrayListMultimap.<Class, EntityChangeListener>create() );
    }

    public void onPostDelete( PostDeleteEvent event )
    {
        final Object entity = event.getEntity();
        final Class entityType = entity.getClass();

        final List<EntityChangeListener> entityListeners = listeners.get( entityType );
        for ( EntityChangeListener listener : entityListeners )
        {
            try
            {
                listener.entityDeleted( event.getSession(), entity );
            }
            catch ( Exception e )
            {
                LOG.error( "Exception in post-commit-delete event listener for entity '" + entityType.getSimpleName() + "' in " +
                               listener.getClass().getName(), e );
            }
        }
    }

    public void onPostInsert( PostInsertEvent event )
    {
        final Object entity = event.getEntity();
        final Class entityType = entity.getClass();

        final List<EntityChangeListener> entityListeners = listeners.get( entityType );
        for ( EntityChangeListener listener : entityListeners )
        {
            try
            {
                listener.entityInserted( event.getSession(), entity );
            }
            catch ( Exception e )
            {
                LOG.error( "Exception in post-commit-insert event listener for entity '" + entityType.getSimpleName() + "' in " +
                               listener.getClass().getName(), e );
            }
        }

    }

    public void onPostUpdate( PostUpdateEvent event )
    {
        final Object entity = event.getEntity();
        final Class entityType = entity.getClass();

        final List<EntityChangeListener> entityListeners = listeners.get( entityType );
        for ( EntityChangeListener listener : entityListeners )
        {
            try
            {
                listener.entityUpdated( event.getSession(), entity );
            }
            catch ( Exception e )
            {
                LOG.error( "Exception in post-commit-update event listener for entity '" + entityType.getSimpleName() + "' in " +
                               listener.getClass().getName(), e );
            }
        }
    }

    public void addListener( EntityChangeListener listener, Class entityType )
    {
        this.listeners.put( entityType, listener );
    }


    public static EntityPostCommitListenerHub getInstance()
    {
        return INSTANCE;
    }
}
