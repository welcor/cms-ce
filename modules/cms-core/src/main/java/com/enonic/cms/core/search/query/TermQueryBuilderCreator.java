package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.index.util.ValueConverter;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class TermQueryBuilderCreator
    extends BaseQueryBuilder
{
    public TermQueryBuilderCreator()
    {
    }

    public static QueryBuilder buildTermQuery( QueryPath path, Object singleValue )
    {
        final QueryBuilder termQuery;

        final boolean isWildCardPath = path.isWildCardPath();

        if ( isWildCardPath )
        {
            path.setMatchAllPath();
        }

        //HANDLE NUMERIC WILDCARD

        if ( singleValue instanceof Number && !isWildCardPath )
        {
            Number number = (Number) singleValue;
            termQuery = QueryBuilders.termQuery( path.getPath() + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX, number );
        }
        else if ( singleValue instanceof Number )
        {
            Number number = (Number) singleValue;
            termQuery = QueryBuilders.termQuery( QueryFieldNameResolver.resolveQueryFieldName( path.getPath() ), number );
        }
        else if ( path.isDateField() && "".equals( singleValue ) )
        {
            MissingFilterBuilder filter = FilterBuilders.missingFilter( path.getPath() );
            termQuery = QueryBuilders.filteredQuery( matchAllQuery(), filter );
        }
        else if ( isValidDateString( singleValue ) )
        {
            termQuery = QueryBuilders.termQuery( path.getPath(), singleValue );
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

    private static boolean isValidDateString( Object value )
    {
        if ( value instanceof String )
        {
            ReadableDateTime date = ValueConverter.toDate( (String) value );
            return date != null;
        }
        return false;
    }

}
