/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.statistical.StatisticalFacetBuilder;

import com.enonic.cms.core.search.query.factory.FilterQueryBuilderFactory;

public class AggregatedQueryTranslator
{
    public static final String AGGREGATED_FACET_NAME = "aggregatedQuery";

    private final FilterQueryBuilderFactory filterQueryBuilderFactory = new FilterQueryBuilderFactory();

    public SearchSourceBuilder build( final AggregatedQuery query )
    {
        final SearchSourceBuilder builder = new SearchSourceBuilder();

        final QueryField aggregateField = new QueryField( QueryFieldNameResolver.resolveQueryFieldName( query.getField() ) );

        builder.query( QueryBuilders.matchAllQuery() );
        builder.size( 0 );

        final StatisticalFacetBuilder facetBuilder =
            new StatisticalFacetBuilder( AGGREGATED_FACET_NAME ).field( aggregateField.getFieldNameForNumericQueries() );

        applyFilters( query, facetBuilder );

        builder.facet( facetBuilder );

        return builder;
    }

    private void applyFilters( final AggregatedQuery query, final StatisticalFacetBuilder facetBuilder )
    {
        final FilterBuilder filterToApply = filterQueryBuilderFactory.buildFilter( query );

        if ( filterToApply != null )
        {
            facetBuilder.facetFilter( filterToApply );
        }
    }


}
