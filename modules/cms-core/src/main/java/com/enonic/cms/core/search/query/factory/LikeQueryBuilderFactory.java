/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query.factory;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.query.QueryFieldAndValue;

public class LikeQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildLikeQuery( final QueryFieldAndValue queryFieldAndValue )
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