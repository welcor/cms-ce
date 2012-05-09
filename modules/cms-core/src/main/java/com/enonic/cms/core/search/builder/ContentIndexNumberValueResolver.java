package com.enonic.cms.core.search.builder;

public class ContentIndexNumberValueResolver
{
    public static Double resolveNumberValue( Object value )
    {
        try
        {
            return Double.parseDouble( value.toString() );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

}
