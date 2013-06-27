/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

public class ContentIndexDataFieldAndValue<T>
{
    private final T value;

    private final String fieldName;

    protected ContentIndexDataFieldAndValue( final String fieldName, final T value )
    {
        this.value = value;
        this.fieldName = fieldName;
    }

    public T getValue()
    {
        return value;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}
