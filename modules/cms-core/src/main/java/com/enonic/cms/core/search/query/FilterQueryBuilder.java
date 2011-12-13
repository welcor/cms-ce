package com.enonic.cms.core.search.query;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:27 AM
 */
public class FilterQueryBuilder
        extends BaseQueryBuilder {

    private final static boolean cacheFilters = true;

    public static void buildFilterQuery(SearchSourceBuilder builder, ContentIndexQuery query) {
        boolean applyFilter = false;

        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        final Collection<ContentKey> contentFilter = query.getContentFilter();

        if (contentFilter != null && !contentFilter.isEmpty()) {
            applyFilter = true;
            boolFilterBuilder.must(buildContentFilter(contentFilter));
        }

        if (query.hasSectionFilter()) {
            applyFilter = true;
            boolFilterBuilder.must(buildSectionFilter(query.getSectionFilter()));
        }

        if (query.hasCategoryFilter()) {
            applyFilter = true;
            boolFilterBuilder.must(buildCategoryFilter(query.getCategoryFilter()));
        }

        if (query.hasContentTypeFilter()) {
            applyFilter = true;
            boolFilterBuilder.must(buildContentTypeFilter(query.getContentTypeFilter()));
        }

        if (applyFilter) {
            builder.filter(boolFilterBuilder);
        } else {
            builder.filter(FilterBuilders.matchAllFilter());
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

    private static TermsFilterBuilder buildContentTypeFilter(Collection<ContentTypeKey> contentTypeFilter) {
        return new TermsFilterBuilder(QueryFieldNameResolver.getContentTypeKeyNumericFieldName(),
                getKeysAsList(contentTypeFilter).toArray());
    }

    private static TermsFilterBuilder buildContentFilter(Collection<ContentKey> contentKeys) {
        return new TermsFilterBuilder(QueryFieldNameResolver.getNumericField("key"),
                getKeysAsList(contentKeys).toArray());
    }


    private static TermsFilterBuilder buildSectionFilter(Collection<MenuItemEntity> menuItemEntities) {
        return new TermsFilterBuilder(QueryFieldNameResolver.getSectionKeyNumericFieldName(),
                getSectionKeysAsList(menuItemEntities).toArray());
    }

    private static TermsFilterBuilder buildCategoryFilter(Collection<CategoryKey> keys) {
        return new TermsFilterBuilder(QueryFieldNameResolver.getCategoryKeyNumericFieldName(), getKeysAsList(keys).toArray());
    }

    private static <T> List<String> getKeysAsList(Collection<T> keys) {
        List<String> keysAsStringList = new ArrayList<String>();

        for (T key : keys) {
            keysAsStringList.add(key.toString());
        }

        return keysAsStringList;

    }

    private static List<String> getSectionKeysAsList(Collection<MenuItemEntity> menuItemEntities) {
        List<String> menuItemKeysAsString = new ArrayList<String>();

        for (MenuItemEntity entity : menuItemEntities) {
            menuItemKeysAsString.add("" + entity.getKey());
        }

        return menuItemKeysAsString;
    }

}
