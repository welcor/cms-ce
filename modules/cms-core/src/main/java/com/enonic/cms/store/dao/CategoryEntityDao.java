/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;
import java.util.SortedMap;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.support.EntityPageList;

@Repository("categoryDao")
public final class CategoryEntityDao
    extends AbstractBaseEntityDao<CategoryEntity>
    implements CategoryDao
{

    private CacheFacade entityCache;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    public void deleteCategory( CategoryEntity category )
    {
        category.setDeleted( true );
        if ( category.getParent() != null )
        {
            sessionFactory.getCache().evictCollection( CategoryEntity.class.getName() + ".children", category.getParent().getKey() );
        }
    }

    public CategoryEntity findByKey( CategoryKey key )
    {
        CategoryEntity category = get( CategoryEntity.class, key );

        if ( category == null )
        {
            return null;
        }

        if ( category.isDeleted() )
        {
            return null;
        }
        return category;
    }

    public SortedMap<CategoryKey, CategoryEntity> findByKeys( final List<CategoryKey> contentKeys )
    {
        final FindCategoryByKeysCommand command = new FindCategoryByKeysCommand( entityCache, getHibernateTemplate(),
                                                                                 new FindCategoryByKeysQuerier(
                                                                                     getHibernateTemplate().getSessionFactory().getCurrentSession() ) );
        return command.execute( contentKeys );
    }

    public List<CategoryEntity> findRootCategories()
    {
        return findByNamedQuery( CategoryEntity.class, "CategoryEntity.findAllRootCategories" );
    }

    public List<CategoryEntity> findRootCategories( List<GroupKey> groupKeys )
    {
        List<String> groupKeysStr = GroupKey.convertToStringList( groupKeys );
        return findByNamedQuery( CategoryEntity.class, "CategoryEntity.findRootCategories", "groupKeys", groupKeysStr );
    }

    public EntityPageList<CategoryEntity> findAll( int index, int count )
    {
        return findPageList( CategoryEntity.class, "x.deleted = 0", index, count );
    }

    @Override
    public long countChildrenByCategory( CategoryEntity category )
    {
        return findSingleByNamedQuery( Long.class, "CategoryEntity.countChildrenByCategoryKey", new String[]{"categoryKey"},
                                       new Object[]{category.getKey()} );
    }

    @Autowired
    public void setCacheManager( CacheManager cacheManager )
    {
        this.entityCache = cacheManager.getOrCreateCache( "entity" );
    }
}