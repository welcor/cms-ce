package com.enonic.cms.core.search.builder;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.NumericUtils;

import com.enonic.cms.core.search.ElasticSearchUtils;

public class ContentIndexOrderbyValueResolver
{
    public static String getNumericOrderBy( Number value )
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

    public static String getOrderbyValueForDate( Date value )
    {
        return ElasticSearchUtils.formatDateAsStringIgnoreTimezone( value );
    }

    public static String getOrderbyValueForString( String value )
    {
        return StringUtils.lowerCase( value );
    }
}
