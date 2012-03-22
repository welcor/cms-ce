package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class TermQueryBuilderCreator
    extends BaseQueryBuilder
{
    public TermQueryBuilderCreator()
    {
    }

    public static QueryBuilder buildTermQuery( final QueryPath path, final QueryValue queryValue )
    {
        final boolean isWildCardPath = path.isWildCardPath();

        if ( isWildCardPath )
        {
            path.setMatchAllPath();
        }

        final QueryBuilder termQuery = doBuildTermQuery( path, queryValue, isWildCardPath );

        if ( path.doRenderAsHasChildQuery() )
        {
            return wrapInHasChildQuery( path, termQuery );
        }
        else
        {
            return termQuery;
        }
    }

    private static QueryBuilder doBuildTermQuery( QueryPath path, QueryValue queryValue, boolean wildCardPath )
    {
        if ( path.doBuildAsIdQuery() )
        {
            return doRenderIdQuery( queryValue );
        }

        if ( queryValue.isNumeric() )
        {
            return doBuildQueryForNumericValue( path, queryValue, wildCardPath );
        }
        //TODO: Alex, should the "path.isDateField()" - test surround both these date-expressions?
        if ( path.isDateField() && queryValue.isEmpty() )
        {
            MissingFilterBuilder filter = FilterBuilders.missingFilter( path.getPath() );
            return QueryBuilders.filteredQuery( matchAllQuery(), filter );
        }
        if ( path.isDateField() && queryValue.isValidDateString() )
        {
            return QueryBuilders.termQuery( path.getPath(), queryValue.getDateAsStringValue() );
        }

        return QueryBuilders.termQuery( path.getPath(), queryValue.getStringValueNormalized() );
    }

    private static QueryBuilder doBuildQueryForNumericValue( final QueryPath path, final QueryValue queryValue, final boolean wildCardPath )
    {
        if ( !wildCardPath )
        {
            return QueryBuilders.termQuery( path.getPath() + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX, queryValue.getDoubleValue() );
        }
        else
        {
            return QueryBuilders.termQuery( QueryFieldNameResolver.resolveQueryFieldName( path.getPath() ), queryValue.getDoubleValue() );
        }
    }

    private static QueryBuilder doRenderIdQuery( final QueryValue queryValue )
    {
        if ( queryValue.isNumeric() )
        {
            return QueryBuilders.idsQuery( IndexType.Content.toString() ).addIds( queryValue.getNumericValueAsString() );
        }

        return QueryBuilders.idsQuery( IndexType.Content.toString() ).addIds( queryValue.getStringValueNormalized() );
    }


}
