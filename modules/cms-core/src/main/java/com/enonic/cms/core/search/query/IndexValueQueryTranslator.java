package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.cms.core.content.index.IndexValueQuery;

public class IndexValueQueryTranslator
{

    private final FilterQueryBuilderFactory filterQueryBuilderFactory = new FilterQueryBuilderFactory();

    // Selects the values from a given field in index for all contents matching filter
    public SearchSourceBuilder build( IndexValueQuery query )
    {
        final SearchSourceBuilder builder = new SearchSourceBuilder();

        final String path = QueryFieldNameResolver.resolveQueryFieldName( query.getField() );
        final QueryPath queryPath = QueryPathResolver.resolveQueryPath( path );

        builder.from( query.getIndex() );
        builder.size( query.getCount() );

        builder.fields( queryPath.getPath() );

        builder.query( QueryBuilders.matchAllQuery() );

        filterQueryBuilderFactory.buildFilterQuery( builder, query );

        //TODO: Fix orderby
        // Orderby
        String orderBy = "x.orderValue " + ( query.isDescOrder() ? "DESC" : "ASC" );

        return builder;

    }


}
