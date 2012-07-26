package com.enonic.cms.core.search;

import org.apache.commons.lang.StringUtils;

public class IndexValueNormalizer
{

    public static String normalizeStringValue( final String stringValue )
    {
        if ( StringUtils.isBlank( stringValue ) )
        {
            return "";
        }

        return stringValue.toLowerCase();
    }


}
