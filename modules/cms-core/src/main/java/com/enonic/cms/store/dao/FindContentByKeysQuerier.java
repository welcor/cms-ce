package com.enonic.cms.store.dao;


import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

class FindContentByKeysQuerier
{
    private Session hibernateSession;

    FindContentByKeysQuerier( Session hibernateSession )
    {
        this.hibernateSession = hibernateSession;
    }

    List<ContentEntity> queryContent( final Collection<ContentKey> contentKeys )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "c" );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( "c.mainVersion", null, SelectBuilder.LEFT_JOIN_FETCH, null );
        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentKey>( "c.key", contentKeys )
        {
            public void appendValue( StringBuffer sql, ContentKey value )
            {
                sql.append( value.toString() );
            }
        }.toString() );

        Query compiled = hibernateSession.createQuery( hqlQuery.toString() );
        compiled.setReadOnly( true );
        compiled.setCacheable( false );
        //noinspection unchecked
        return compiled.list();
    }
}
