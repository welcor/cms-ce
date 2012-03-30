package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class InQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildInQuery( QueryPath path, QueryValue[] values )
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( QueryValue value : values )
        {
            if ( value.isNumeric() )
            {
                boolQuery.should( QueryBuilders.termQuery( path.getPath(), value.getDoubleValue() ) );
            }
            else
            {
                boolQuery.should( QueryBuilders.termQuery( path.getPath(), value.getStringValueNormalized() ) );
            }
        }

        return boolQuery;
    }
}
