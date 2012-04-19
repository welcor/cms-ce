package com.enonic.cms.core.search.index;

public class ContentIndexDataFieldValue<T>
{
    private final T value;

    private final String fieldName;

    protected ContentIndexDataFieldValue( final String fieldName, final T value )
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
