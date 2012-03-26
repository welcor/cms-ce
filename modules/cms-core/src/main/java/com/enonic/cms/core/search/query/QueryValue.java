package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.ISODateTimeFormat;


public class QueryValue
{
    private final Double doubleValue;

    private final String stringValue;

    private final ReadableDateTime dateTimeValue;


    public QueryValue( Object value )
    {
        if ( value instanceof Number )
        {
            doubleValue = ( (Number) value ).doubleValue();
            stringValue = value.toString();
            dateTimeValue = null;
        }
        else if ( value instanceof ReadableDateTime )
        {
            dateTimeValue = toUTCTimeZone((ReadableDateTime) value);
            stringValue = formatDateForElasticSearch( dateTimeValue );
            doubleValue = null;
        }
        else
        {
            stringValue = value.toString();
            doubleValue = null;
            dateTimeValue = null;
        }
    }

    public Double getDoubleValue()
    {
        return doubleValue;
    }

    public boolean isNumeric()
    {
        return doubleValue != null;
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
        return stringValue != null ? StringUtils.lowerCase( stringValue ) : null;
    }

    public String getNumericValueAsString()
    {
        return stringValue != null ? stringValue.substring( 0, stringValue.indexOf( '.' ) ) : null;
    }

    public String getDateAsStringValue()
    {
        return dateTimeValue != null ? stringValue : null;
    }

    public boolean isEmpty()
    {
        return StringUtils.isBlank( stringValue );
    }

    private String formatDateForElasticSearch( final ReadableDateTime date )
    {

        return ISODateTimeFormat.dateTime().print( date );
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
