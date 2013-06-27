/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

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
