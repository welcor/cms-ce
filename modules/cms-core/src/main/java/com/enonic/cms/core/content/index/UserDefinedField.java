/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.search.query.SimpleText;

public final class UserDefinedField
{
    private final String name;

    private final SimpleText value;

    public UserDefinedField( String name, SimpleText value )
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public SimpleText getValue()
    {
        return this.value;
    }

}
