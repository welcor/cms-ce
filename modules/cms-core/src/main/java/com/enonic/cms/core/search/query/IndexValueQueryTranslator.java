package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.search.query.factory.FilterQueryBuilderFactory;

public class IndexValueQueryTranslator
{

    private final FilterQueryBuilderFactory filterQueryBuilderFactory = new FilterQueryBuilderFactory();

    // Selects the values from a given field in index for all contents matching filter
    public SearchSourceBuilder build( final IndexValueQuery query, QueryField queryField )
    {
        final SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.from( query.getIndex() );
        builder.size( query.getCount() );

        //builder.fields( queryField.getFieldName() );

        builder.query( QueryBuilders.matchAllQuery() );

        filterQueryBuilderFactory.buildFilterQuery( builder, query );
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
