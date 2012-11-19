package com.enonic.cms.core.search.facet.model;

import java.util.Date;

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

        final Date dateValue = ContentIndexDateValueResolver.resolveDateValue( value );

        if ( dateValue != null )
        {
            return new FacetRangeDateValue( dateValue );
        }

        throw new IllegalArgumentException( "Not a numeric or valid date-value: " + value );
    }

}
