package com.enonic.cms.core.search.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.ContentSearchQuery;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:27 AM
 */
public class FilterQueryBuilder
        extends BaseQueryBuilder
{

    public static void buildFilterQuery( SearchSourceBuilder builder, ContentSearchQuery query )
    {
        boolean category = false, contenttype = false, section = false;

        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        if ( query.hasSectionFilter() )
        {
            section = true;
            boolFilterBuilder.must( buildSectionFilter( query.getSectionFilter() ) );
        }

        if ( query.hasCategoryFilter() )
        {
            category = true;
            boolFilterBuilder.must( buildCategoryFilter( query.getCategoryFilter() ) );
        }

        if ( query.hasContentTypeFilter() )
        {
            contenttype = true;
            boolFilterBuilder.must( buildContentTypeFilter( query.getContentTypeFilter() ) );
        }

        final boolean applyFilter = section || category || contenttype;
        if ( applyFilter )
        {
            builder.filter( boolFilterBuilder );
        }
        else
        {
            builder.filter( FilterBuilders.matchAllFilter() );
        }
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

    private static TermsFilterBuilder buildContentTypeFilter( Set<ContentTypeKey> contentTypeFilter )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getContentTypeKeyNumericFieldName(),
                                       getContentKeysAsIntList( contentTypeFilter ).toArray() );
    }

    private static List<String> getContentKeysAsIntList( Set<ContentTypeKey> contentTypeKeys )
    {
        List<String> contentKeysAsStrings = new ArrayList<String>();

        for ( ContentTypeKey key : contentTypeKeys )
        {
            contentKeysAsStrings.add( key.toString() );
        }

        return contentKeysAsStrings;
    }

    //private Filter and( Filter f1, Filter f2 )
    // {
    /*   if ( ( f1 != null ) && ( f2 != null ) )
    {
        final BooleanFilter combined = new BooleanFilter();
        combined.add( new FilterClause( f1, BooleanClause.Occur.MUST ) );
        combined.add( new FilterClause( f2, BooleanClause.Occur.MUST ) );
        return combined;
    }

    if ( f1 == null )
    {
        return f2;
    }
    else
    {
        return f1;
    }
    */
    //}

    private static TermsFilterBuilder buildSectionFilter( Set<MenuItemKey> keys )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getSectionKeyNumericFieldName(),
                                       getSectionKeysAsList( keys ).toArray() );
    }

    private static TermsFilterBuilder buildCategoryFilter( Set<CategoryKey> keys )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getCategoryKeyNumericFieldName(),
                                       getCategoryKeysAsList( keys ).toArray() );
    }

    private static List<String> getCategoryKeysAsList( Set<CategoryKey> categoryKeys )
    {
        List<String> categoryKeysAsStrings = new ArrayList<String>();

        for ( CategoryKey key : categoryKeys )
        {
            categoryKeysAsStrings.add( key.toString() );
        }

        return categoryKeysAsStrings;
    }

    private static List<String> getSectionKeysAsList( Set<MenuItemKey> menuItemKeys )
    {
        List<String> menuItemKeysAsString = new ArrayList<String>();

        for ( MenuItemKey key : menuItemKeys )
        {
            menuItemKeysAsString.add( key.toString() );
        }

        return menuItemKeysAsString;
    }

    /*

    private Filter buildContentTypeFilter( Set<ContentTypeKey> keys )
    {
        final TermsFilter filter = new TermsFilter();
        for ( ContentTypeKey key : keys )
        {
            filter.addTerm( new Term( CONTENT_TYPE_KEY_FIELD, key.toString() ) );
        }

        return filter;
    }
    */


}
