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

    public static QueryBuilder buildRangeQuery( String field, Object lower, Object upper, boolean lowerInclusive,
                                                boolean upperInclusive )
    {
        final boolean doStringComparison = lower instanceof String || upper instanceof String;
        if ( doStringComparison )
        {
            return rangeQuery( field ).from( lower ).to( upper ).includeLower( lowerInclusive ).includeUpper(
                    upperInclusive );
        }

        Double lowerNumeric = getNumericValue( lower );
        Double upperNumeric = getNumericValue( upper );

        if ( lowerNumeric == null && upperNumeric == null )
        {
            throw new IllegalArgumentException( "Invalid lower and upper - values in range query" );
        }

        return rangeQuery( field + NUMERIC_FIELD_POSTFIX ).from( lowerNumeric ).to( upperNumeric ).includeLower(
                lowerInclusive ).includeUpper( upperInclusive );
    }

}
