package com.enonic.cms.core.search.builder.indexdata;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.NumericUtils;
import org.joda.time.DateTime;

import com.enonic.cms.core.search.ElasticSearchUtils;

public class ContentIndexOrderbyValueResolver
{
    public static String resolveOrderbyValue( Set<Object> values )
    {
        if ( values == null )
        {
            return null;
        }

        for ( Object value : values )
        {
            if ( value != null )
            {
                if ( value instanceof Number )
                {
                    return getOrderValueForNumber( (Number) value );
                }

                if ( value instanceof Date )
                {
                    return ElasticSearchUtils.formatDateForElasticSearch( new DateTime( value ) );
                }

                if ( value instanceof DateTime )
                {
                    return ElasticSearchUtils.formatDateForElasticSearch( (DateTime) value );
                }

                return StringUtils.lowerCase( value.toString() );
            }
        }

        return null;
    }


    public static String getOrderValueForNumber( Number value )
    {
        if ( value == null )
        {
            return null;
        }

        String orderValue;

        if ( value instanceof Double )
        {
            orderValue = NumericUtils.doubleToPrefixCoded( value.doubleValue() );
        }

        else if ( value instanceof Float )
        {
            orderValue = NumericUtils.floatToPrefixCoded( value.floatValue() );
        }

        else if ( value instanceof Long )
        {
            orderValue = NumericUtils.longToPrefixCoded( value.longValue() );
        }
        else
        {
            orderValue = NumericUtils.intToPrefixCoded( value.intValue() );
        }

        return orderValue;
    }


}
