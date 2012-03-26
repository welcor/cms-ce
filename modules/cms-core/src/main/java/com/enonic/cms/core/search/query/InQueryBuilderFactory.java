package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class InQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildInQuery( String field, QueryValue[] values )
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( QueryValue value : values )
        {
            if ( value.isNumeric() )
            {
                boolQuery.should(
                    QueryBuilders.termQuery( field + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX, value.getDoubleValue() ) );
            }
            else
            {
                boolQuery.should( QueryBuilders.termQuery( field, value.getStringValueNormalized() ) );
            }
        }

        return boolQuery;
    }
}
