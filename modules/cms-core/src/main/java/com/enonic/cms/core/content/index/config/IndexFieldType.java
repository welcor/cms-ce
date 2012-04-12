package com.enonic.cms.core.content.index.config;

public enum IndexFieldType
{
    STRING,
    NUMBER,
    DATE;


    public static IndexFieldType getValue( String value )
    {
        try
        {
            return valueOf( value.toUpperCase() );
        }
        catch ( Exception e )
        {
            return STRING;
        }
    }


    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
