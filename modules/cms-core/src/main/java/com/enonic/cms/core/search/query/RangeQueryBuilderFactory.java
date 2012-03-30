package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.joda.time.ReadableDateTime;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;


public class RangeQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    public QueryBuilder buildRangeQuery( final QueryPath queryPath, final QueryValue lower, final QueryValue upper,
                                         final boolean lowerInclusive, final boolean upperInclusive )
    {
        final boolean isNumericComparison = ( lower != null && lower.isNumeric() ) || ( upper != null && upper.isNumeric() );
        final boolean isDateComparison =
            !isNumericComparison && ( ( lower != null && lower.isDateTime() ) || ( upper != null && upper.isDateTime() ) );
        final boolean doStringComparison = !( isNumericComparison || isDateComparison );

        if ( doStringComparison )
        {
            return buildRangeQueryString( queryPath, lower, upper, lowerInclusive, upperInclusive );
        }
        else if ( isNumericComparison )
        {
            Double lowerNumeric = lower != null ? lower.getDoubleValue() : null;
            Double upperNumeric = upper != null ? upper.getDoubleValue() : null;

            return buildRangeQueryNumeric( queryPath, lowerNumeric, upperNumeric, lowerInclusive, upperInclusive );
        }
        else
        {
            ReadableDateTime lowerDateTime = lower != null ? lower.getDateTime() : null;
            ReadableDateTime upperDateTime = upper != null ? upper.getDateTime() : null;

            return buildRangeQueryDateTime( queryPath, lowerDateTime, upperDateTime, lowerInclusive, upperInclusive );
        }
    }

    private QueryBuilder buildRangeQueryDateTime( QueryPath queryPath, ReadableDateTime lowerDateTime, ReadableDateTime upperDateTime,
                                                  boolean lowerInclusive, boolean upperInclusive )
    {
        if ( lowerDateTime == null && upperDateTime == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }

        final String queryName = queryPath.isWildCardPath() ? ALL_USERDATA_FIELDNAME : queryPath.getPath();
        return rangeQuery( queryName ).
            from( lowerDateTime ).
            to( upperDateTime ).
            includeLower( lowerInclusive ).
            includeUpper( upperInclusive );
    }

    private QueryBuilder buildRangeQueryNumeric( QueryPath queryPath, Double lowerNumeric, Double upperNumeric, boolean lowerInclusive,
                                                 boolean upperInclusive )
    {
        if ( lowerNumeric == null && upperNumeric == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }

        final String queryName = queryPath.isWildCardPath() ? ALL_USERDATA_FIELDNAME : queryPath.getPath();
        return rangeQuery( queryName ).
            from( lowerNumeric ).
            to( upperNumeric ).
            includeLower( lowerInclusive ).
            includeUpper( upperInclusive );
    }

    private QueryBuilder buildRangeQueryString( QueryPath queryPath, QueryValue lower, QueryValue upper, boolean lowerInclusive,
                                                boolean upperInclusive )
    {
        if ( lower == null && upper == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }
        final String queryName = queryPath.isWildCardPath() ? ALL_USERDATA_FIELDNAME : queryPath.getPath();
        return rangeQuery( queryName ).
            from( lower != null ? lower.getStringValueNormalized() : null ).
            to( upper != null ? upper.getStringValueNormalized() : null ).
            includeLower( lowerInclusive ).
            includeUpper( upperInclusive );
    }

}
