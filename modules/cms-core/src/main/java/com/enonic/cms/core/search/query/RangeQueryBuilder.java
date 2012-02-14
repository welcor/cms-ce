package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/12/11
 * Time: 4:50 PM
 */
public class RangeQueryBuilder
    extends BaseQueryBuilder
{
    protected static final String NUMERIC_FIELD_POSTFIX = "_numeric";

    public static QueryBuilder buildRangeQuery( final String field, final QueryValue lower, final QueryValue upper,
                                                final boolean lowerInclusive, final boolean upperInclusive )
    {
        final boolean doStringComparison = ( lower != null && !lower.isNumeric() ) || ( upper != null && !upper.isNumeric() );

        if ( doStringComparison )
        {
            return rangeQuery( field ).from( lower != null ? lower.getStringValueNormalized() : null )
                .to( upper != null ? upper.getStringValueNormalized() : null )
                .includeLower( lowerInclusive )
                .includeUpper( upperInclusive );
        }

        Double lowerNumeric = lower != null ? lower.getDoubleValue() : null;
        Double upperNumeric = upper != null ? upper.getDoubleValue() : null;

        if ( lowerNumeric == null && upperNumeric == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }

        return rangeQuery( field + NUMERIC_FIELD_POSTFIX ).from( lowerNumeric )
            .to( upperNumeric )
            .includeLower( lowerInclusive )
            .includeUpper( upperInclusive );
    }

}
