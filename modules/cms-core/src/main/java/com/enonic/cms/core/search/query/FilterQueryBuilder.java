/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;


public class FilterQueryBuilder
    extends BaseQueryBuilder
{

    public void buildFilterQuery( SearchSourceBuilder builder, ContentIndexQuery contentIndexQuery )
    {
        List<FilterBuilder> filtersToApply = getListOfFiltersToApply( contentIndexQuery );

        doAddFilters( builder, filtersToApply );
    }

    public void buildFilterQuery( SearchSourceBuilder builder, IndexValueQuery query )
    {
        List<FilterBuilder> filtersToApply = getListOfFiltersToApply( query );

        doAddFilters( builder, filtersToApply );
    }

    private List<FilterBuilder> getListOfFiltersToApply( ContentIndexQuery query )
    {
        List<FilterBuilder> filtersToApply = new ArrayList<FilterBuilder>();

        if ( query.getContentFilter() != null && !query.getContentFilter().isEmpty() )
        {
            filtersToApply.add( buildContentFilter( query.getContentFilter() ) );
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

        if ( query.getContentOnlineAtFilter() != null )
        {
            final FilterBuilder publishedAtFilter = buildContentPublishedAtFilter( query.getContentOnlineAtFilter() );
            filtersToApply.add( publishedAtFilter );
        }

        if ( query.hasContentStatusFilter() )
        {
            filtersToApply.add( buildContentStatusFilter( query.getContentStatusFilter() ) );
        }

        if ( query.getCategoryAccessTypeFilter() != null && !query.getCategoryAccessTypeFilter().isEmpty() )
        {

            final FilterBuilder categoryAccessFilter =
                buildCategoryAccessTypeFilter( query.getCategoryAccessTypeFilter(), query.getCategoryAccessTypeFilterPolicy(),
                                               query.getSecurityFilter() );
            if ( categoryAccessFilter != null )
            {
                filtersToApply.add( categoryAccessFilter );
            }
        }

        if ( query.hasSecurityFilter() )
        {
            final FilterBuilder securityFilter = buildSecurityFilter( query.getSecurityFilter() );
            filtersToApply.add( securityFilter );
        }

        return filtersToApply;
    }

    private List<FilterBuilder> getListOfFiltersToApply( IndexValueQuery query )
    {
        List<FilterBuilder> filtersToApply = new ArrayList<FilterBuilder>();

        if ( query.hasCategoryFilter() )
        {
            filtersToApply.add( buildCategoryFilter( query.getCategoryFilter() ) );
        }

        if ( query.hasContentTypeFilter() )
        {
            filtersToApply.add( buildContentTypeFilter( query.getContentTypeFilter() ) );
        }

        if ( query.hasSecurityFilter() )
        {
            final FilterBuilder securityFilter = buildSecurityFilter( query.getSecurityFilter() );
            filtersToApply.add( securityFilter );
        }

        return filtersToApply;
    }

    private void doAddFilters( SearchSourceBuilder builder, List<FilterBuilder> filtersToApply )
    {

        if ( filtersToApply.isEmpty() )
        {
            return;
        }

        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        if ( filtersToApply.size() == 1 )
        {
            builder.filter( filtersToApply.get( 0 ) );
        }
        else
        {
            for ( FilterBuilder filter : filtersToApply )
            {
                boolFilterBuilder.must( filter );
            }

            builder.filter( boolFilterBuilder );
        }
    }


    private FilterBuilder buildContentPublishedAtFilter( final DateTime dateTime )
    {
        final ReadableDateTime dateTimeRoundedDownToNearestMinute = toUTCTimeZone( dateTime.minuteOfHour().roundFloorCopy() );
        final RangeFilterBuilder publishFromFilter = FilterBuilders.rangeFilter( "publishfrom" ).lte( dateTimeRoundedDownToNearestMinute );

        final MissingFilterBuilder publishToMissing = FilterBuilders.missingFilter( "publishto" );
        final RangeFilterBuilder publishToGTDate = FilterBuilders.rangeFilter( "publishto" ).gt( dateTimeRoundedDownToNearestMinute );
        final OrFilterBuilder publishToFilter = FilterBuilders.orFilter( publishToMissing, publishToGTDate );

        final AndFilterBuilder filter = FilterBuilders.andFilter( publishFromFilter, publishToFilter );
        return filter;
    }

    private ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }

    private FilterBuilder buildSecurityFilter( final Collection<GroupKey> groupKeys )
    {
        final String[] groups = new String[groupKeys.size()];
        int i = 0;
        for ( GroupKey groupKey : groupKeys )
        {
            groups[i] = groupKey.toString().toLowerCase();
            i++;
        }
        final TermsFilterBuilder securityFilter =
            FilterBuilders.termsFilter( IndexFieldNameConstants.CONTENT_ACCESS_READ_FIELDNAME, groups );
        return securityFilter;
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

    private TermFilterBuilder buildContentStatusFilter( Integer contentStatus )
    {
        return new TermFilterBuilder( QueryFieldNameResolver.getContentStatusQueryFieldName(), contentStatus );
    }

    private TermsFilterBuilder buildContentTypeFilter( Collection<ContentTypeKey> contentTypeFilter )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getContentTypeKeyQueryFieldName(),
                                       getKeysAsList( contentTypeFilter ).toArray() );
    }

    private TermsFilterBuilder buildContentFilter( Collection<ContentKey> contentKeys )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getContentKeyQueryFieldName(), getKeysAsList( contentKeys ).toArray() );
    }

    private FilterBuilder buildCategoryAccessTypeFilter( final Collection<CategoryAccessType> categoryAccessTypeFilter,
                                                         ContentIndexQuery.CategoryAccessTypeFilterPolicy policy )
    {
        if ( categoryAccessTypeFilter.size() == 1 )
        {
            return new TermFilterBuilder( QueryFieldNameResolver.getCategoryAccessTypeFieldName(),
                                          categoryAccessTypeFilter.iterator().next() );
        }

        final boolean must = policy.equals( ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );

        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        for ( CategoryAccessType type : categoryAccessTypeFilter )
        {

            final TermFilterBuilder term = new TermFilterBuilder( QueryFieldNameResolver.getCategoryAccessTypeFieldName(), type );

            if ( must )
            {
                boolFilterBuilder.must( term );
            }
            else
            {
                boolFilterBuilder.should( term );
            }
        }

        return boolFilterBuilder;
    }

    private FilterBuilder buildCategoryAccessTypeFilter( final Collection<CategoryAccessType> categoryAccessTypeFilter,
                                                         ContentIndexQuery.CategoryAccessTypeFilterPolicy policy,
                                                         Collection<GroupKey> securityFilter )
    {
        // cannot apply category access type filter without security filter
        if ( ( categoryAccessTypeFilter == null ) || ( securityFilter == null ) )
        {
            return null;
        }

        final String[] groups = new String[securityFilter.size()];
        int i = 0;
        for ( GroupKey groupKey : securityFilter )
        {
            groups[i] = groupKey.toString().toLowerCase();
            i++;
        }

        if ( categoryAccessTypeFilter.size() == 1 )
        {
            CategoryAccessType type = categoryAccessTypeFilter.iterator().next();
            return new TermFilterBuilder( QueryFieldNameResolver.getCategoryAccessTypeFieldName( type ), groups );
        }

        final boolean must = policy.equals( ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );

        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        for ( CategoryAccessType type : categoryAccessTypeFilter )
        {

            final TermFilterBuilder term = new TermFilterBuilder( QueryFieldNameResolver.getCategoryAccessTypeFieldName( type ), groups );

            if ( must )
            {
                boolFilterBuilder.must( term );
            }
            else
            {
                boolFilterBuilder.should( term );
            }
        }

        return boolFilterBuilder;
    }

    private FilterBuilder buildSectionFilter( ContentIndexQuery query )
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

    private TermsFilterBuilder buildCategoryFilter( Collection<CategoryKey> keys )
    {
        return new TermsFilterBuilder( QueryFieldNameResolver.getCategoryKeyQueryFieldName(), getKeysAsList( keys ).toArray() );
    }

    private <T> List<Integer> getKeysAsList( Collection<T> keys )
    {
        List<Integer> keysAsStringList = new ArrayList<Integer>();

        for ( T key : keys )
        {
            keysAsStringList.add( new Integer( key.toString() ) );
        }

        return keysAsStringList;

    }

    private List<Integer> getSectionKeysAsList( Collection<MenuItemEntity> menuItemEntities )
    {
        List<Integer> menuItemKeysAsString = new ArrayList<Integer>();

        for ( MenuItemEntity entity : menuItemEntities )
        {
            menuItemKeysAsString.add( entity.getKey().toInt() );
        }

        return menuItemKeysAsString;
    }

}
