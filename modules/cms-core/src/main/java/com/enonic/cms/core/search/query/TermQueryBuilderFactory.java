package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.IndexType;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class TermQueryBuilderFactory
    extends BaseQueryBuilderFactory
{
    public TermQueryBuilderFactory()
    {
    }

    public QueryBuilder buildTermQuery( final QueryFieldAndValue queryFieldAndValue )
    {

        final QueryBuilder termQuery = doBuildTermQuery( queryFieldAndValue );

        if ( queryFieldAndValue.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( queryFieldAndValue, termQuery );
        }
        else
        {
            return termQuery;
        }
    }

    private QueryBuilder doBuildTermQuery( final QueryFieldAndValue queryFieldAndValue )
    {
        if ( queryFieldAndValue.doBuildAsIdQuery() )
        {
            return buildAsIdQuery( queryFieldAndValue );
        }

        if ( queryFieldAndValue.doBuildAsEmptyDateFieldQuery() )
        {
            MissingFilterBuilder filter = FilterBuilders.missingFilter( queryFieldAndValue.getFieldName() );
            return QueryBuilders.filteredQuery( matchAllQuery(), filter );
        }

        return QueryBuilders.termQuery( queryFieldAndValue.getFieldName(), queryFieldAndValue.getValue() );
    }

    private QueryBuilder buildAsIdQuery( final QueryFieldAndValue queryFieldAndValue )
    {
        return QueryBuilders.idsQuery( IndexType.Content.toString() ).addIds( queryFieldAndValue.getValueForIdQuery() );
    }


}
