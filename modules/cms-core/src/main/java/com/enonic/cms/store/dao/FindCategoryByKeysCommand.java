package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.OrderCategoryKeysByGivenOrderComparator;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;

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

    SortedMap<CategoryKey, CategoryEntity> execute( final List<CategoryKey> categoryKeys )
    {
        final SortedMap<CategoryKey, CategoryEntity> categoryMapByKey =
            new TreeMap<CategoryKey, CategoryEntity>( new OrderCategoryKeysByGivenOrderComparator( categoryKeys ) );
        final List<CategoryKey> categoriesNotFoundInCache = new ArrayList<CategoryKey>();

        findCategoriesInCache( categoryKeys, categoryMapByKey, categoriesNotFoundInCache );

        if ( !categoriesNotFoundInCache.isEmpty() )
        {
            final List<CategoryEntity> categoriesFromDB = findCategoryByKeysQuerier.queryCategories( categoriesNotFoundInCache );
            for ( CategoryEntity c : categoriesFromDB )
            {
                categoryMapByKey.put( c.getKey(), c );
            }
        }
        return categoryMapByKey;
    }

    private void findCategoriesInCache( Iterable<CategoryKey> categoryKeys, Map<CategoryKey, CategoryEntity> categoriesFoundInCache,
                                        List<CategoryKey> categoriesNotFoundInCache )
    {
        for ( final CategoryKey categoryKey : categoryKeys )
        {
            final boolean categoryExistsInCache = categoryExistInCacheResolver.categoryExistsInCache( categoryKey );
            if ( categoryExistsInCache )
            {
                final CategoryEntity categoryFoundInCache = (CategoryEntity) hibernateTemplate.get( CategoryEntity.class, categoryKey );
                if ( categoryFoundInCache != null )
                {
                    categoriesFoundInCache.put( categoryFoundInCache.getKey(), categoryFoundInCache );
                }
            }
            else
            {
                categoriesNotFoundInCache.add( categoryKey );
            }
        }
    }
}
