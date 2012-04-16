package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class LikeQueryBuilderFactory
    extends BaseQueryBuilderFactory
{
    public LikeQueryBuilderFactory()
    {
    }

    // TODO: Refactor to use QueryPathValue
    public QueryBuilder buildLikeQuery( QueryField queryField, QueryValue value )
    {
        final boolean isWildcardPath = queryField.isWildcardQueyField();

        if ( isWildcardPath )
        {
            queryField.setMatchAllPath();
        }

        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery( queryField.getFieldName(), value.getWildcardValue() );

        if ( queryField.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( new QueryFieldAndValue( queryField, value ), queryBuilder );
        }
        else
        {
            return queryBuilder;
        }
    }
}