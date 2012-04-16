package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class InQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildInQuery( QueryField queryField, QueryValue[] values )
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( QueryValue value : values )
        {
            if ( value.isNumeric() )
            {
                boolQuery.should( QueryBuilders.termQuery( queryField.getFieldName(), value.getNumericValue() ) );
            }
            else
            {
                boolQuery.should( QueryBuilders.termQuery( queryField.getFieldName(), value.getStringValueNormalized() ) );
            }
        }

        return boolQuery;
    }
}
