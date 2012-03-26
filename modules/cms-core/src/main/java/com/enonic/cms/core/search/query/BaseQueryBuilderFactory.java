package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public abstract class BaseQueryBuilderFactory
{

    protected QueryBuilder wrapInHasChildQuery( QueryPath path, QueryBuilder query )
    {
        return QueryBuilders.hasChildQuery( path.getIndexType().toString(), query );
    }

}
