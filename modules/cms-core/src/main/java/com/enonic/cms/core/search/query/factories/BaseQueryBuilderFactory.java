package com.enonic.cms.core.search.query.factories;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;
import com.enonic.cms.core.search.query.QueryFieldAndValue;

public abstract class BaseQueryBuilderFactory
    extends IndexFieldNameConstants
{

    protected QueryBuilder wrapInHasChildQuery( QueryFieldAndValue queryFieldAndValue, QueryBuilder query )
    {
        return QueryBuilders.hasChildQuery( queryFieldAndValue.getIndexType(), query );
    }

}
