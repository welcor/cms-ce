package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.joda.time.DateTime;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

public class RangeQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildRangeQuery( final QueryField queryField, final QueryValue lower, final QueryValue upper,
                                         final boolean lowerInclusive, final boolean upperInclusive )
    {
        final boolean isNumericComparison = ( lower != null && lower.isNumeric() ) || ( upper != null && upper.isNumeric() );
        final boolean isDateComparison =
            !isNumericComparison && ( ( lower != null && lower.isDateTime() ) || ( upper != null && upper.isDateTime() ) );
        final boolean doStringComparison = !( isNumericComparison || isDateComparison );

        if ( doStringComparison )
        {
            return buildRangeQueryString( queryField, lower, upper, lowerInclusive, upperInclusive );
        }
        else if ( isNumericComparison )
        {
            Number lowerNumeric = lower != null ? lower.getNumericValue() : null;
            Number upperNumeric = upper != null ? upper.getNumericValue() : null;

            return buildRangeQueryNumeric( queryField, lowerNumeric, upperNumeric, lowerInclusive, upperInclusive );
        }
        else
        {
            DateTime lowerDateTime = lower != null ? lower.getDateTime().toDateTime() : null;
            DateTime upperDateTime = upper != null ? upper.getDateTime().toDateTime() : null;

            return buildRangeQueryDateTime( queryField, lowerDateTime, upperDateTime, lowerInclusive, upperInclusive );
        }
    }

    private QueryBuilder buildRangeQueryDateTime( QueryField queryField, DateTime lowerDateTime, DateTime upperDateTime,
                                                  boolean lowerInclusive, boolean upperInclusive )
    {
        if ( lowerDateTime == null && upperDateTime == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }

        final String queryName = queryField.isWildcardQueyField() ? ALL_USERDATA_FIELDNAME : queryField.getFieldName();
        return rangeQuery( queryName ).
            from( lowerDateTime ).
            to( upperDateTime ).
            includeLower( lowerInclusive ).
            includeUpper( upperInclusive );
    }

    private QueryBuilder buildRangeQueryNumeric( QueryField queryField, Number lowerNumeric, Number upperNumeric, boolean lowerInclusive,
                                                 boolean upperInclusive )
    {
        if ( lowerNumeric == null && upperNumeric == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }

        final String queryName = queryField.isWildcardQueyField() ? ALL_USERDATA_FIELDNAME : queryField.getFieldName();
        return rangeQuery( queryName ).
            from( lowerNumeric ).
            to( upperNumeric ).
            includeLower( lowerInclusive ).
            includeUpper( upperInclusive );
    }

    private QueryBuilder buildRangeQueryString( QueryField queryField, QueryValue lower, QueryValue upper, boolean lowerInclusive,
                                                boolean upperInclusive )
    {
        if ( lower == null && upper == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }
        final String queryName = queryField.isWildcardQueyField() ? ALL_USERDATA_FIELDNAME : queryField.getFieldName();
        return rangeQuery( queryName ).
            from( lower != null ? lower.getStringValueNormalized() : null ).
            to( upper != null ? upper.getStringValueNormalized() : null ).
            includeLower( lowerInclusive ).
            includeUpper( upperInclusive );
    }

}
