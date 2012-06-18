package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

public class ContentIndexDataElement
    extends IndexFieldNameConstants
{
    private String fieldBaseName;

    private final Set<Date> dateTimeValues = Sets.newHashSet();

    private final Set<Double> numericValues = Sets.newHashSet();

    private final Set<String> stringValues = Sets.newHashSet();

    private String orderBy;

    public String getFieldBaseName()
    {
        return fieldBaseName;
    }

    public void setFieldBaseName( final String fieldBaseName )
    {
        this.fieldBaseName = fieldBaseName;
    }

    public void setOrderBy( final String orderBy )
    {
        this.orderBy = orderBy;
    }

    public void addNumericValue( Double value )
    {
        this.numericValues.add( value );
    }

    public void addDateValue( Date value )
    {
        this.dateTimeValues.add( value );
    }

    public void addStringValue( String value )
    {
        this.stringValues.add( value );
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public Set<Date> getDateTimeValues()
    {
        return dateTimeValues;
    }

    public Set<Double> getNumericValues()
    {
        return numericValues;
    }

    public Set<String> getStringValues()
    {
        return stringValues;
    }
}
