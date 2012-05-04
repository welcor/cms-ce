package com.enonic.cms.core.search.query.factories;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.query.QueryField;
import com.enonic.cms.core.search.query.QueryFieldAndValue;
import com.enonic.cms.core.search.query.QueryValue;

public class InQueryBuilderFactory
    extends BaseQueryBuilderFactory
{
    public QueryBuilder buildInQuery( final QueryField queryField, final QueryValue[] values )
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( QueryValue value : values )
        {
            boolQuery.should( new TermQueryBuilderFactory().buildTermQuery( new QueryFieldAndValue( queryField, value ) ) );
        }

        return boolQuery;
    }
}
