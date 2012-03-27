package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class LikeQueryBuilderFactory
    extends BaseQueryBuilderFactory
{
    public LikeQueryBuilderFactory()
    {
    }

    public QueryBuilder buildLikeQuery( QueryPath path, QueryValue value )
    {
        final boolean isWildcardPath = path.isWildCardPath();

        if ( isWildcardPath )
        {
            path.setMatchAllPath();
        }

        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery( path.getPath(), value.getWildcardValue() );

        if ( path.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( path, queryBuilder );
        }
        else
        {
            return queryBuilder;
        }
    }
}