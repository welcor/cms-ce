/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.search.query.QueryFieldAndMultipleValues;
import com.enonic.cms.core.search.query.QueryFieldAndValue;
import com.enonic.cms.core.search.query.QueryFieldFactory;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;
import com.enonic.cms.core.search.query.QueryValue;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;


public class FilterQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public void buildFilterQuery( final SearchSourceBuilder builder, final ContentIndexQuery contentIndexQuery )
    {
        List<FilterBuilder> filtersToApply = getListOfFiltersToApply( contentIndexQuery );

        doAddFilters( builder, filtersToApply );
    }

    public void buildFilterQuery( final SearchSourceBuilder builder, final IndexValueQuery query )
    {
        List<FilterBuilder> filtersToApply = getListOfFiltersToApply( query );

        doAddFilters( builder, filtersToApply );
    }

    public FilterBuilder buildFilterForAggregatedQuery( final AggregatedQuery query )
    {
        final List<FilterBuilder> listOfFiltersToApply = getListOfFiltersToApply( query );

        return createFilter( listOfFiltersToApply );
    }

    private List<FilterBuilder> getListOfFiltersToApply( final ContentIndexQuery query )
    {
        List<FilterBuilder> filtersToApply = new ArrayList<FilterBuilder>();

        if ( query.hasSectionFilter() )
        {
            filtersToApply.add( buildSectionFilter( query ) );
        }
        else if ( query.isSectionFilter() )
        {
            filtersToApply.add( buildAllSectionsFilter( query ) );
        }

        if ( query.getContentFilter() != null && !query.getContentFilter().isEmpty() )
        {
            filtersToApply.add( buildContentFilter( query.getContentFilter() ) );
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
            filtersToApply.add( buildContentPublishedAtFilter( query.getContentOnlineAtFilter() ) );
        }

        if ( query.hasContentStatusFilter() )
        {
            filtersToApply.add( buildContentStatusFilter( query.getContentStatusFilter() ) );
        }

        if ( query.hasSecurityFilter() )
        {
            filtersToApply.add( buildSecurityFilter( query.getSecurityFilter() ) );
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

        return filtersToApply;
    }

    private List<FilterBuilder> getListOfFiltersToApply( final IndexValueQuery query )
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

    private List<FilterBuilder> getListOfFiltersToApply( final AggregatedQuery query )
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


    private void doAddFilters( final SearchSourceBuilder builder, final List<FilterBuilder> filtersToApply )
    {
        final FilterBuilder filter = createFilter( filtersToApply );

        if ( filter == null )
        {
            return;
        }

        builder.filter( filter );
    }

    private FilterBuilder createFilter( final List<FilterBuilder> filtersToApply )
    {
        if ( filtersToApply.isEmpty() )
        {
            return null;
        }

        if ( filtersToApply.size() == 1 )
        {
            return filtersToApply.get( 0 );
        }
        else
        {
            BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

            for ( FilterBuilder filter : filtersToApply )
            {
                boolFilterBuilder.must( filter );
            }

            return boolFilterBuilder;
        }
    }

    private FilterBuilder buildContentPublishedAtFilter( final DateTime dateTime )
    {
        final ReadableDateTime dateTimeRoundedDownToNearestMinute = toUTCTimeZone( dateTime.minuteOfHour().roundFloorCopy() );

        final RangeFilterBuilder publishFromFilter =
            FilterBuilders.rangeFilter( QueryFieldFactory.resolveQueryField( PUBLISH_FROM_FIELDNAME ).getFieldNameForDateQueries() ).lte(
                dateTimeRoundedDownToNearestMinute );

        final MissingFilterBuilder publishToMissing =
            FilterBuilders.missingFilter( QueryFieldFactory.resolveQueryField( PUBLISH_TO_FIELDNAME ).getFieldNameForDateQueries() );
        final RangeFilterBuilder publishToGTDate =
            FilterBuilders.rangeFilter( QueryFieldFactory.resolveQueryField( PUBLISH_TO_FIELDNAME ).getFieldNameForDateQueries() ).gt(
                dateTimeRoundedDownToNearestMinute );
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
        return buildTermsFilterForValues( getKeysAsQueryValues( groupKeys ), CONTENT_ACCESS_READ_FIELDNAME );
    }

    private TermFilterBuilder buildContentStatusFilter( final Integer contentStatus )
    {
        QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( STATUS_FIELDNAME, contentStatus.toString() );

        return new TermFilterBuilder( queryFieldAndValue.getFieldName(), queryFieldAndValue.getValue() );
    }

    private FilterBuilder buildContentTypeFilter( final Collection<ContentTypeKey> contentTypeFilter )
    {
        return buildTermsFilterForValues( getKeysAsQueryValues( contentTypeFilter ), CONTENTTYPE_KEY_FIELDNAME );
    }

    private FilterBuilder buildContentFilter( final Collection<ContentKey> contentKeys )
    {
        return buildTermsFilterForValues( getKeysAsQueryValues( contentKeys ), CONTENTKEY_FIELDNAME );
    }

    private FilterBuilder buildCategoryFilter( final Collection<CategoryKey> keys )
    {
        return buildTermsFilterForValues( getKeysAsQueryValues( keys ), CATEGORY_KEY_FIELDNAME );
    }


    private FilterBuilder buildCategoryAccessTypeFilter( final Collection<CategoryAccessType> categoryAccessTypeFilter,
                                                         final ContentIndexQuery.CategoryAccessTypeFilterPolicy policy,
                                                         final Collection<GroupKey> securityFilter )
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

    private FilterBuilder buildSectionFilter( final ContentIndexQuery query )
    {
        final Set<QueryValue> keysAsQueryValues = getSectionKeysAsList( query.getSectionFilter() );

        final boolean buildBothApprovedAndUnapproved = !query.isApprovedSectionContentOnly() && !query.isUnapprovedSectionContentOnly();
        if ( buildBothApprovedAndUnapproved )
        {
            return buildBothApprovedAndUnapprovedSectionFilter( keysAsQueryValues );
        }

        String fieldName = query.isApprovedSectionContentOnly() ? CONTENTLOCATION_APPROVED_FIELDNAME : CONTENTLOCATION_UNAPPROVED_FIELDNAME;

        return buildTermsFilterForValues( keysAsQueryValues, fieldName );
    }

    private FilterBuilder buildBothApprovedAndUnapprovedSectionFilter( final Set<QueryValue> keysAsQueryValues )
    {
        BoolFilterBuilder boolFilterBuilder = FilterBuilders.boolFilter();

        boolFilterBuilder.should( buildTermsFilterForValues( keysAsQueryValues, CONTENTLOCATION_APPROVED_FIELDNAME ) );
        boolFilterBuilder.should( buildTermsFilterForValues( keysAsQueryValues, CONTENTLOCATION_UNAPPROVED_FIELDNAME ) );

        return boolFilterBuilder;
    }

    private FilterBuilder buildTermsFilterForValues( final Set<QueryValue> keysAsQueryValues, final String fieldName )
    {
        final QueryFieldAndMultipleValues queryFieldAndMultipleValues = new QueryFieldAndMultipleValues( fieldName, keysAsQueryValues );

        return new TermsFilterBuilder( queryFieldAndMultipleValues.getFieldName(), queryFieldAndMultipleValues.getValues() );
    }


    private FilterBuilder buildAllSectionsFilter( final ContentIndexQuery query )
    {
        if ( query.isApprovedSectionContentOnly() )
        {
            return FilterBuilders.existsFilter( CONTENTLOCATION_APPROVED_FIELDNAME );
        }
        else if ( query.isUnapprovedSectionContentOnly() )
        {
            return FilterBuilders.existsFilter( CONTENTLOCATION_UNAPPROVED_FIELDNAME );
        }
        else
        {
            return FilterBuilders.matchAllFilter();
        }
    }

    private <T> Set<QueryValue> getKeysAsQueryValues( final Collection<T> keys )
    {
        Set<QueryValue> queryValues = Sets.newHashSet();

        for ( T key : keys )
        {
            queryValues.add( new QueryValue( key.toString() ) );
        }

        return queryValues;

    }


    private Set<QueryValue> getSectionKeysAsList( final Collection<MenuItemEntity> menuItemEntities )
    {
        Set<QueryValue> menuItemKeysAsString = Sets.newHashSet();

        for ( MenuItemEntity entity : menuItemEntities )
        {
            menuItemKeysAsString.add( new QueryValue( entity.getKey().toString() ) );
        }

        return menuItemKeysAsString;
    }
}
