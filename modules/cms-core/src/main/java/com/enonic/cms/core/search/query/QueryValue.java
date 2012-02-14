package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.index.util.ValueConverter;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/13/12
 * Time: 3:25 PM
 */
public class QueryValue
{
    private Double doubleValue;

    private String stringValue;


    public QueryValue( Object value )
    {
        if ( value instanceof Number )
        {
            doubleValue = ( (Number) value ).doubleValue();
        }

        stringValue = value.toString();
    }

    public Double getDoubleValue()
    {
        return doubleValue;
    }

    public boolean isNumeric()
    {
        return doubleValue != null;
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
        return stringValue != null ? StringUtils.lowerCase( stringValue ) : null;
    }


    public boolean isValidDateString()
    {
        if ( this.isNumeric() )
        {
            return false;
        }

        ReadableDateTime date = ValueConverter.toDate( stringValue );
        return date != null;
    }

    public boolean isEmpty()
    {
        return StringUtils.isBlank( stringValue );
    }

}
