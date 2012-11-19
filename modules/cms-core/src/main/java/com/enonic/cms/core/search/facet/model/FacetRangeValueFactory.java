package com.enonic.cms.core.search.facet.model;

import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.search.ElasticSearchFormatter;
import com.enonic.cms.core.search.builder.ContentIndexDateValueResolver;
import com.enonic.cms.core.search.builder.ContentIndexNumberValueResolver;

public class FacetRangeValueFactory
{

    public static FacetRangeValue createFacetRangeValue( String value )
    {
        final Double doubleValue = ContentIndexNumberValueResolver.resolveNumberValue( value );

        if ( value == null )
        {
            return null;
        }

        if ( doubleValue != null )
        {
            return new FacetRangeNumericValue( doubleValue );
        }

        final ReadableDateTime readableDateTime = ContentIndexDateValueResolver.resolveReadableDateTimeValue( value );

        if ( readableDateTime != null )
        {
            ReadableDateTime UTCTime = ElasticSearchFormatter.toUTCTimeZone( readableDateTime );

            return new FacetRangeDateValue( UTCTime );
        }

        throw new IllegalArgumentException( "Not a numeric or valid date-value: " + value );
    }

}
