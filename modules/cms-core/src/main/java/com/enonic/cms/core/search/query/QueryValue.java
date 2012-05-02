package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.search.ElasticSearchUtils;


public class QueryValue
{
    private final Number numericValue;

    private final String stringValue;

    private final ReadableDateTime dateTimeValue;


    public QueryValue( Object value )
    {
        if ( value instanceof Number )
        {
            numericValue = (Number) value;
            stringValue = doNormalizeString( value.toString() );
            dateTimeValue = null;
        }
        else if ( value instanceof ReadableDateTime )
        {
            dateTimeValue = toUTCTimeZone( (ReadableDateTime) value );
            stringValue = doNormalizeString( ElasticSearchUtils.formatDateForElasticSearch( (ReadableDateTime) value ) );
            numericValue = null;
        }
        else
        {
            stringValue = doNormalizeString( value == null ? "" : value.toString() );
            numericValue = null;
            dateTimeValue = null;
        }
    }

    public Double getNumericValue()
    {
        return numericValue != null ? numericValue.doubleValue() : null;
    }

    public boolean isNumeric()
    {
        return numericValue != null;
    }

    public ReadableDateTime getDateTime()
    {
        return dateTimeValue;
    }

    public boolean isDateTime()
    {
        return dateTimeValue != null;
    }

    public String getStringValueNormalized()
    {
//        Assert.isTrue( !isDateTime(), "Attempt to use QueryValue with date-time content as a string value: " + dateTimeValue );

        if ( isWildcardValue() )
        {
            return getWildcardValue();
        }

        return stringValue != null ? StringUtils.lowerCase( stringValue ) : null;
    }

    public boolean isWildcardValue()
    {
        return StringUtils.contains( this.stringValue, "%" );
    }

    public String getWildcardValue()
    {
        return StringUtils.replace( this.stringValue, "%", "*" );
    }

    private String doNormalizeString( String stringValue )
    {
        return StringUtils.lowerCase( stringValue );
    }

    public String getNumericValueAsString()
    {
        return stringValue != null ? stringValue.substring( 0, stringValue.indexOf( '.' ) ) : null;
    }

    public boolean isEmpty()
    {
        return StringUtils.isBlank( stringValue ) && dateTimeValue == null && numericValue == null;
    }

    private ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }
}
