package com.enonic.cms.core.search;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class IndexFieldnameNormalizer
    extends IndexFieldNameConstants
{
    public static String normalizeFieldName( final String stringValue )
    {
        if ( StringUtils.isBlank( stringValue ) )
        {
            return "";
        }

        String normalized = replaceSeparators( stringValue );
        normalized = replaceFieldTypeSeparators( normalized );
        normalized = removeAttributeSeparator( normalized );

        return normalized.toLowerCase();
    }

    private static String replaceSeparators( final String stringValue )
    {
        return StringUtils.replace( stringValue, QUERYLANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR );
    }

    private static String replaceFieldTypeSeparators( final String stringValue )
    {
        return StringUtils.replace( stringValue, INDEX_FIELD_TYPE_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR );
    }

    private static String removeAttributeSeparator( final String stringValue )
    {
        return StringUtils.remove( stringValue, "@" );
    }


}
