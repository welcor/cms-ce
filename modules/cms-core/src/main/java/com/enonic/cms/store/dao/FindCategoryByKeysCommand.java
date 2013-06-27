/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryMap;

class FindCategoryByKeysCommand
{
    private CategoryExistInCacheResolver categoryExistInCacheResolver;

    private HibernateTemplate hibernateTemplate;

    private FindCategoryByKeysQuerier findCategoryByKeysQuerier;

    FindCategoryByKeysCommand( CacheFacade entityCache, HibernateTemplate hibernateTemplate,
                               FindCategoryByKeysQuerier findCategoryByKeysQuerier )
    {
        this.categoryExistInCacheResolver = new CategoryExistInCacheResolver( entityCache );
        this.hibernateTemplate = hibernateTemplate;
        this.findCategoryByKeysQuerier = findCategoryByKeysQuerier;
    }

    CategoryMap execute( final List<CategoryKey> categoryKeys )
    {
        final CategoryMap categoryMap = new CategoryMap( categoryKeys );

        final List<CategoryKey> categoriesNotFoundInCache = findCategoriesInCache( categoryKeys, categoryMap );

        if ( !categoriesNotFoundInCache.isEmpty() )
        {
            final List<CategoryEntity> categoriesFromDB = findCategoryByKeysQuerier.queryCategories( categoriesNotFoundInCache );
            categoryMap.addAll( categoriesFromDB );
        }
        return categoryMap;
    }

    private List<CategoryKey> findCategoriesInCache( Iterable<CategoryKey> categoryKeys, CategoryMap categoriesFoundInCache )
    {
        final List<CategoryKey> categoriesNotFoundInCache = new ArrayList<CategoryKey>();
        for ( final CategoryKey categoryKey : categoryKeys )
        {
            final boolean categoryExistsInCache = categoryExistInCacheResolver.categoryExistsInCache( categoryKey );
            if ( categoryExistsInCache )
            {
                final CategoryEntity categoryFoundInCache = hibernateTemplate.get( CategoryEntity.class, categoryKey );
                if ( categoryFoundInCache != null )
                {
                    categoriesFoundInCache.add( categoryFoundInCache );
                }
            }
            else
            {
                categoriesNotFoundInCache.add( categoryKey );
            }
        }
        return categoriesNotFoundInCache;
    }
}
