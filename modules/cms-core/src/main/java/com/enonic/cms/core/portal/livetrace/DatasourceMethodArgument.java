/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

public class DatasourceMethodArgument
{
    private MaxLengthedString name = new MaxLengthedString();

    private MaxLengthedString value = new MaxLengthedString();

    DatasourceMethodArgument( String name, String value )
    {
        this.name = new MaxLengthedString( name );
        this.value = new MaxLengthedString( value );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getValue()
    {
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    // TODO: Remove?
    public String getOverride()
    {
        return null;
    }
}
