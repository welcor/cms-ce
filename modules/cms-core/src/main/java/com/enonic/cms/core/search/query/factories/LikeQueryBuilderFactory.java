package com.enonic.cms.core.search.query.factories;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.query.QueryFieldAndValue;

public class LikeQueryBuilderFactory
    extends BaseQueryBuilderFactory
{
    public LikeQueryBuilderFactory()
    {
    }

    // TODO: Refactor to use QueryPathValue
    public QueryBuilder buildLikeQuery( QueryFieldAndValue queryFieldAndValue )
    {

        QueryBuilder queryBuilder =
            QueryBuilders.wildcardQuery( queryFieldAndValue.getFieldName(), queryFieldAndValue.getValue().toString() );

        if ( queryFieldAndValue.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( queryFieldAndValue, queryBuilder );
        }
        else
        {
            return queryBuilder;
        }
    }
}