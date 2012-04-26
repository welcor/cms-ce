package com.enonic.cms.core.search.builder.indexdata;

import java.util.Set;

public class ContentIndexOrderbyResolver
{

    public static String resolveOrderbyValue( Set<Object> values )
    {
        if ( values == null )
        {
            return null;
        }

        for ( Object value : values )
        {
            if ( value != null )
            {
                return value.toString();
            }
        }

        return null;
    }
}
