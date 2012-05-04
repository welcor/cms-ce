package com.enonic.cms.core.search.query.factory;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;
import com.enonic.cms.core.search.query.QueryFieldAndValue;

public abstract class BaseQueryBuilderFactory
    extends IndexFieldNameConstants
{

    protected QueryBuilder wrapInHasChildQuery( final QueryFieldAndValue queryFieldAndValue, final QueryBuilder query )
    {
        return QueryBuilders.hasChildQuery( queryFieldAndValue.getIndexType(), query );
    }

}
