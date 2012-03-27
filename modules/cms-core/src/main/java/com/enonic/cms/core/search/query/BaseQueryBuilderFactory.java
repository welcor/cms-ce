package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public abstract class BaseQueryBuilderFactory
    extends IndexFieldNameConstants
{

    protected QueryBuilder wrapInHasChildQuery( QueryPath path, QueryBuilder query )
    {
        return QueryBuilders.hasChildQuery( path.getIndexType().toString(), query );
    }

}
