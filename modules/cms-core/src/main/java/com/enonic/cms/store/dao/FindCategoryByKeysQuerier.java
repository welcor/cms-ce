/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;

class FindCategoryByKeysQuerier
{
    private Session hibernateSession;

    FindCategoryByKeysQuerier( Session hibernateSession )
    {
        this.hibernateSession = hibernateSession;
    }

    List<CategoryEntity> queryCategories( final Collection<CategoryKey> categoryKeys )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "c" );
        hqlQuery.addFromTable( CategoryEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", new InClauseBuilder<CategoryKey>( "c.key", categoryKeys )
        {
            public void appendValue( StringBuffer sql, CategoryKey value )
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
