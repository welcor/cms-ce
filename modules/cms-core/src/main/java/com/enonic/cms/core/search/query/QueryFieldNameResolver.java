package com.enonic.cms.core.search.query;

import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:16 AM
 */
public class QueryFieldNameResolver
    extends IndexFieldNameConstants
{


    public static String normalizeFieldName( String name )
    {
        final String normalized = name.replace( '/', '.' ).replace( '.', '_' ).replaceAll( "@", "" ).toLowerCase();
        return normalized;
    }

    public static String toFieldName( FieldExpr expression )
    {
        return normalizeFieldName( expression.getPath() );
    }

    public static String getNumericField( String fieldName )
    {
        return fieldName + NUMERIC_FIELD_POSTFIX;
    }

    public static String getOrderByFieldName( FieldExpr expression )
    {
        String propertyName = ORDER_FIELD_PREFIX + normalizeFieldName( expression.getPath() );
        return propertyName;
    }

    public static String getOrderByFieldName( String fieldName )
    {
        String propertyName = ORDER_FIELD_PREFIX + normalizeFieldName( fieldName );
        return propertyName;
    }

    public static String normalizeName( String name )
    {
        return name.replace( QUERY_LANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR ).toLowerCase();
    }

    public static String getCategoryKeyNumericFieldName()
    {
        return CATEGORY_FIELD_PREFIX + getNumericField( "key" );
    }

    public static String getSectionKeyNumericFieldName()
    {
        return SECTION_FIELD_PREFIX + getNumericField( ".menuitemkey" );
    }

    public static String getContentTypeKeyNumericFieldName()
    {
        return "contenttypekey_numeric";
        //return CONTENT_TYPE_PREFIX + getNumericField( "_key" );
    }

}
