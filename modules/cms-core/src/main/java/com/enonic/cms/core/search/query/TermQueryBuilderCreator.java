package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.cms.core.search.ElasticContentConstants;

public class TermQueryBuilderCreator
    extends BaseQueryBuilder
{
    public TermQueryBuilderCreator()
    {
    }

    public static QueryBuilder buildTermQuery( QueryPath path, Object singleValue )
    {
        TermQueryBuilder termQuery;

        if ( path.isWildCardQuery() )
        {
            path.setMatchAllPath();
        }

        if ( singleValue instanceof Number )
        {
            Number number = (Number) singleValue;
            termQuery = QueryBuilders.termQuery( path.getPath() + ElasticContentConstants.NUMERIC_FIELD_POSTFIX, number );
        }
        else
        {
            String stringValue = (String) singleValue;
            termQuery = QueryBuilders.termQuery( path.getPath(), StringUtils.lowerCase( stringValue ) );
        }

        if ( path.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( path, termQuery );
        }
        else
        {
            return termQuery;
        }

    }


}
