package com.enonic.cms.core.search.query.factory;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;
import com.enonic.cms.core.search.query.QueryValue;

public class FullTextQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildFulltextQuery( final String path, final QueryValue queryValue )
    {
        return QueryBuilders.termQuery( path + IndexFieldNameConstants.NON_ANALYZED_FIELD_POSTFIX, queryValue.getStringValueNormalized() );
    }

}
