package com.enonic.cms.core.search.builder;


import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 12:06 PM
 */
final public class IndexFieldNameCreator
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

        return fieldName.replace( QUERY_LANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR )
            .replace( ".", INDEX_FIELDNAME_PROPERTY_SEPARATOR )
            .replaceAll( "@", "" )
            .toLowerCase();
    }


    public static String getNumericFieldName( String fieldName )
    {
        return normalizeFieldName( fieldName ) + NUMERIC_FIELD_POSTFIX;
    }

    public static String getOrderByFieldName( String fieldName )
    {
        return ORDER_FIELD_PREFIX + doNormalizeFieldName( fieldName );
    }

    public static String getCategoryKeyFieldName()
    {
        return CATEGORY_FIELD_PREFIX + "key";
    }

    public static String getCategoryNameFieldName()
    {
        return CATEGORY_FIELD_PREFIX + "_name";
    }

    public static String getContentTypeKeyFieldName()
    {
        return CONTENT_TYPE_PREFIX + "key";
    }

    public static String getContentTypeNameFieldName()
    {
        return CONTENT_TYPE_PREFIX;
    }


}
