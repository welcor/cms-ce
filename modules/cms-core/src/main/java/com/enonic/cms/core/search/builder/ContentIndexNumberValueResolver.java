package com.enonic.cms.core.search.builder;

public class ContentIndexNumberValueResolver
{
    public static Double resolveNumberValue( Object value )
    {
        if ( value == null )
        {
            return null;
        }

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
