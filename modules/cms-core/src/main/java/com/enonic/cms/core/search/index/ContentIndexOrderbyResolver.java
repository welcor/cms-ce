package com.enonic.cms.core.search.index;

import java.util.Set;

public class ContentIndexOrderbyResolver
{

    public static String resolveOrderbyValue( Set<Object> values )
    {
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
