/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.index.config.IndexFieldType;

public final class UserDefinedField
{
    private final String name;

    private final SimpleText value;

    private final IndexFieldType indexFieldType;

    public UserDefinedField( String name, SimpleText value )
    {
        this.name = name;
        this.value = value;
        this.indexFieldType = IndexFieldType.STRING;
    }

    public UserDefinedField( final String name, final SimpleText value, final IndexFieldType indexFieldType )
    {
        this.name = name;
        this.value = value;
        this.indexFieldType = indexFieldType;
    }

    public String getName()
    {
        return this.name;
    }

    public SimpleText getValue()
    {
        return this.value;
    }

    public IndexFieldType getIndexFieldType()
    {
        return indexFieldType;
    }
}
