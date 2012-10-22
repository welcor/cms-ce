package com.enonic.cms.core.portal.datasource2.xml;

public final class ParameterElement
{
    private final String name;

    private String value;

    public ParameterElement( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue( final String value )
    {
        this.value = value;
    }
}
