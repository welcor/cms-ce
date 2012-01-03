package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;

public class LikeQueryBuilderCreator
    extends BaseQueryBuilder
{
    public LikeQueryBuilderCreator()
    {
    }

    public final static QueryBuilder buildLikeQuery( QueryPath path, String value )
    {
        if ( path.isWildCardPath() )
        {
            path.setMatchAllPath();
        }

        final WildcardQueryBuilder wildcardQueryBuilder =
            QueryBuilders.wildcardQuery( path.getPath(), StringUtils.replaceChars( value, '%', '*' ) );

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