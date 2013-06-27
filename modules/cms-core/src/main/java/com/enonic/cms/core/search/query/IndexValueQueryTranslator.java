/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.search.query.factory.FilterQueryBuilderFactory;

public class IndexValueQueryTranslator
{

    private final FilterQueryBuilderFactory filterQueryBuilderFactory = new FilterQueryBuilderFactory();

    public SearchSourceBuilder build( final IndexValueQuery query, QueryField queryField, int count )
    {
        return doBuild( query, queryField, count );
    }

    // Selects the values from a given field in index for all contents matching filter
    public SearchSourceBuilder build( final IndexValueQuery query, QueryField queryField )
    {
        return doBuild( query, queryField, query.getCount() );
    }

    private SearchSourceBuilder doBuild( final IndexValueQuery query, final QueryField queryField, final int size )
    {
        final SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.size( size );

        builder.from( query.getIndex() );

        //builder.fields( queryField.getFieldName() );

        builder.query( QueryBuilders.matchAllQuery() );

        final FilterBuilder filterToApply = filterQueryBuilderFactory.buildFilter( query );

        if ( filterToApply != null )
        {
            builder.filter( filterToApply );
        }

        applySorting( builder, queryField, query.isDescOrder() );

        return builder;
    }

    private void applySorting( final SearchSourceBuilder builder, final QueryField queryField, final boolean isDescOrder )
    {
        final String sortFieldName = queryField.getFieldName();
        final String name = QueryFieldNameResolver.resolveOrderFieldName( new FieldExpr( sortFieldName ) );
        final SortOrder sortOrder = isDescOrder ? SortOrder.DESC : SortOrder.ASC;
        final FieldSortBuilder sorting = new FieldSortBuilder( name ).order( sortOrder ).ignoreUnmapped( true );
        builder.sort( sorting );
    }
}
