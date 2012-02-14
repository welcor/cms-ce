package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.enonic.cms.core.search.builder.IndexValueResolver;

public class LikeQueryBuilderCreator
    extends BaseQueryBuilder
{
    public LikeQueryBuilderCreator()
    {
    }

    public final static QueryBuilder buildLikeQuery( QueryPath path, QueryValue value )
    {
        if ( path.isWildCardPath() )
        {
            path.setMatchAllPath();
        }

        final WildcardQueryBuilder wildcardQueryBuilder =
            QueryBuilders.wildcardQuery( path.getPath(), IndexValueResolver.getWildcardValue( value.getStringValueNormalized() ) );

        if ( path.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( path, wildcardQueryBuilder );
        }
        else
        {
            return wildcardQueryBuilder;
        }
    }
}