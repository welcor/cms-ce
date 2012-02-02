package com.enonic.cms.core.search.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.index.query.BaseFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:27 AM
 */
public class FilterQueryBuilder
    extends BaseQueryBuilder
{

    private final static boolean cacheFilters = true;

    public static void buildFilterQuery( SearchSourceBuilder builder, ContentIndexQuery query )
    {
        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        List<BaseFilterBuilder> filtersToApply = getListOfFiltersToApply( query );

        if ( filtersToApply.isEmpty() )
        {
            // Do nothing
        }
        else if ( filtersToApply.size() == 1 )
        {
            builder.filter( filtersToApply.get( 0 ) );
        }
        else
        {
            for ( BaseFilterBuilder filter : filtersToApply )
            {
                boolFilterBuilder.must( filter );
            }

            builder.filter( boolFilterBuilder );
        }
    }

    private static List<BaseFilterBuilder> getListOfFiltersToApply( ContentIndexQuery query )
    {
        List<BaseFilterBuilder> filtersToApply = new ArrayList<BaseFilterBuilder>();

        final Collection<ContentKey> contentFilter = query.getContentFilter();

        if ( contentFilter != null && !contentFilter.isEmpty() )
        {
            filtersToApply.add( buildContentFilter( contentFilter ) );
        }

        if ( query.hasSectionFilter() )
        {
            filtersToApply.add( buildSectionFilter( query ) );
        }

        if ( query.hasCategoryFilter() )
        {
            filtersToApply.add( buildCategoryFilter( query.getCategoryFilter() ) );
        }

        if ( query.hasContentTypeFilter() )
        {
            filtersToApply.add( buildContentTypeFilter( query.getContentTypeFilter() ) );
        }

        return filtersToApply;
    }

    /*
        private static QueryBuilder buildHasChildQuery( AttachmentFilters attachmentFilter )
        {
            List<AttachmentFilter> attachmentFilters = attachmentFilter.getFilters();
    
            BoolQueryBuilder query = QueryBuilders.boolQuery();
    
            for ( AttachmentFilter filter : attachmentFilters )
            {
                buildAttachmentFilterSubQuery( query, filter );
            }
    
            return query;
        }
    
        private static void buildAttachmentFilterSubQuery( BoolQueryBuilder query, AttachmentFilter filter )
        {
            List<String> filterValues = filter.getValueList();
    
            if ( filterValues.size() == 1 )
            {
                query.must( QueryBuilders.termQuery( filter.getFilterType().getFieldRepresentation(), filterValues.get( 0 ) ) );
                return;
            }
    
            BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
    
            for ( String value : filterValues )
            {
                subQuery.must( QueryBuilders.termQuery( filter.getFilterType().getFieldRepresentation(), value ) );
            }
    
            query.must( subQuery );
        }
    */

    private static TermsFilterBuilder buildContentTypeFilter( Collection<ContentTypeKey> contentTypeFilter )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getContentTypeKeyQueryFieldName(),
                                       getKeysAsList( contentTypeFilter ).toArray() );
    }

    private static TermsFilterBuilder buildContentFilter( Collection<ContentKey> contentKeys )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getContentKeyQueryFieldName(), getKeysAsList( contentKeys ).toArray() );
    }


    private static BaseFilterBuilder buildSectionFilter( ContentIndexQuery query )
    {
        if ( query.isApprovedSectionContentOnly() )
        {
            return new TermsFilterBuilder( QueryFieldNameResolver.getSectionKeysApprovedQueryFieldName(),
                                           getSectionKeysAsList( query.getSectionFilter() ).toArray() );
        }
        else if ( query.isUnapprovedSectionContentOnly() )
        {
            return new TermsFilterBuilder( QueryFieldNameResolver.getSectionKeysUnapprovedQueryFieldName(),
                                           getSectionKeysAsList( query.getSectionFilter() ).toArray() );
        }
        else
        {
            BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();
            boolFilterBuilder.should( new TermsFilterBuilder( QueryFieldNameResolver.getSectionKeysApprovedQueryFieldName(),
                                                              getSectionKeysAsList( query.getSectionFilter() ).toArray() ) );
            boolFilterBuilder.should( new TermsFilterBuilder( QueryFieldNameResolver.getSectionKeysUnapprovedQueryFieldName(),
                                                              getSectionKeysAsList( query.getSectionFilter() ).toArray() ) );

            return boolFilterBuilder;
        }
    }

    private static TermsFilterBuilder buildCategoryFilter( Collection<CategoryKey> keys )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getCategoryKeyQueryFieldName(), getKeysAsList( keys ).toArray() );
    }

    private static <T> List<String> getKeysAsList( Collection<T> keys )
    {
        List<String> keysAsStringList = new ArrayList<String>();

        for ( T key : keys )
        {
            keysAsStringList.add( key.toString() );
        }

        return keysAsStringList;

    }

    private static List<String> getSectionKeysAsList( Collection<MenuItemEntity> menuItemEntities )
    {
        List<String> menuItemKeysAsString = new ArrayList<String>();

        for ( MenuItemEntity entity : menuItemEntities )
        {
            menuItemKeysAsString.add( "" + entity.getKey() );
        }

        return menuItemKeysAsString;
    }

}
