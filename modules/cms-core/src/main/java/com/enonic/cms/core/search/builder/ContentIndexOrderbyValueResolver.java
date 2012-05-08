package com.enonic.cms.core.search.builder;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.NumericUtils;

import com.enonic.cms.core.search.ElasticSearchFormatter;

public class ContentIndexOrderbyValueResolver
{
    public static String getOrderbyValue( Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof Number )
        {
            return getNumericOrderBy( (Number) value );
        }

        if ( value instanceof Date )
        {
            return getOrderbyValueForDate( (Date) value );
        }

        final Double doubleValue = ContentIndexNumberValueResolver.resolveNumberValue( value );

        if ( doubleValue != null )
        {
            return getNumericOrderBy( doubleValue );
        }

        return getOrderbyValueForString( value.toString() );
    }


    private static String getNumericOrderBy( Number value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof Double )
        {
            return NumericUtils.doubleToPrefixCoded( (Double) value );
        }

        if ( value instanceof Float )
        {
            return NumericUtils.floatToPrefixCoded( (Float) value );
        }

        if ( value instanceof Long )
        {
            return NumericUtils.longToPrefixCoded( (Long) value );
        }

        return NumericUtils.intToPrefixCoded( value.intValue() );
    }

    private static String getOrderbyValueForDate( Date value )
    {
        return ElasticSearchFormatter.formatDateAsStringFull( value );
    }

    private static String getOrderbyValueForString( String value )
    {
        return StringUtils.lowerCase( value );
    }
}
