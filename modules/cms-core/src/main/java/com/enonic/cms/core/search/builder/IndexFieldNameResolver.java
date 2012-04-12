package com.enonic.cms.core.search.builder;


import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 12:06 PM
 */
final public class IndexFieldNameResolver
    extends IndexFieldNameConstants
{

    public static String normalizeFieldName( final String fieldName )
    {
        return doNormalizeFieldName( fieldName );
    }

    private static String doNormalizeFieldName( final String fieldName )
    {
        if ( StringUtils.isBlank( fieldName ) )
        {
            return "";
        }

        return fieldName.replace( QUERY_LANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR ).replace( ".",
                                                                                                                   INDEX_FIELDNAME_PROPERTY_SEPARATOR ).replaceAll(
            "@", "" ).toLowerCase();
    }


    public static String getNumericsFieldName( final String fieldBaseName )
    {
        return doNormalizeFieldName( fieldBaseName ) + ".number";
    }

    public static String getDateFieldName( final String fieldBaseName )
    {
        return doNormalizeFieldName( fieldBaseName ) + ".date";
    }

}
