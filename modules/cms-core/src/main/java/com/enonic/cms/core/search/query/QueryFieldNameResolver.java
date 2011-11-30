package com.enonic.cms.core.search.query;

import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.search.ElasticContentConstants;
import com.enonic.cms.core.search.ElasticIndexUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:16 AM
 */
public class QueryFieldNameResolver
        extends ElasticContentConstants
{


    public static String normalizeFieldName( String name )
    {
        final String normalized = name.replace( '/', '.' ).replace( '.', '_' ).replaceAll( "@", "" ).toLowerCase();
        return normalized;
    }

    public static String toFieldName( FieldExpr expression )
    {
        return ElasticIndexUtils.toPropertyName( expression.getPath() );
    }

    public static String getNumericField( String fieldName )
    {
        return fieldName + NUMERIC_FIELD_POSTFIX;
    }

    public static String getOrderByFieldName( FieldExpr expression )
    {
        String propertyName = ORDER_FIELD_PREFIX + normalizeName( expression.getPath() );
        return propertyName;
    }

    public static String getOrderByFieldName( String fieldName )
    {
        String propertyName = ORDER_FIELD_PREFIX + normalizeName( fieldName );
        return propertyName;
    }

    public static String toPropertyName( String value )
    {
        return PROPERTY_FIELD_PREFIX + normalizeName( value );
    }

    public static String normalizeName( String name )
    {
        return name.replace( QUERY_LANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR ).toLowerCase();
    }

    public static String getCategoryKeyFieldName()
    {
        return CATEGORY_FIELD_PREFIX + "_key";
    }

    public static String getCategoryKeyNumericFieldName()
    {
        return CATEGORY_FIELD_PREFIX + getNumericField( "_key" );
    }

    public static String getSectionKeyNumericFieldName()
    {
        return SECTION_FIELD_PREFIX + getNumericField( ".menuitemkey" );
    }

    public static String getCategoryNameFieldName()
    {
        return CATEGORY_FIELD_PREFIX + "_name";
    }


    public static String getContentTypeKeyFieldName()
    {
        return CONTENT_TYPE_PREFIX + "_key";
    }


    public static String getContentTypeKeyNumericFieldName()
    {
        return CONTENT_TYPE_PREFIX + getNumericField( "_key" );
    }


    public static String getContentTypeNameFieldName()
    {
        return CONTENT_TYPE_PREFIX + "_name";
    }

}
