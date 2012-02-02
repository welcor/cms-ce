package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

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

    public static String resolveQueryFieldName( String name )
    {
        return doNormalizeQueryFieldName( name );
    }

    private static String doNormalizeQueryFieldName( String name )
    {
        String normalized = name.replace( '/', '.' ).replace( '.', '_' ).replaceAll( "@", "" ).toLowerCase();

        if ( StringUtils.startsWith( normalized, CONTENTDATA_ALIAS ) )
        {
            normalized = StringUtils.replaceOnce( normalized, CONTENTDATA_ALIAS, CUSTOMDATA_KEY );
        }

        return normalized;
    }

    public static String resolveQueryFieldName( FieldExpr expression )
    {
        return resolveQueryFieldName( expression.getPath() );
    }

    public static String getOrderByFieldName( FieldExpr expression )
    {
        return getOrderByFieldName( expression.getPath() );
    }

    public static String getOrderByFieldName( String fieldName )
    {
        return ORDER_FIELD_PREFIX + resolveQueryFieldName( fieldName );
    }

    public static String getSectionKeysApprovedQueryFieldName()
    {
        return CONTENTLOCATION_APPROVED_FIELDNAME + NUMERIC_FIELD_POSTFIX;
    }

    public static String getSectionKeysUnapprovedQueryFieldName()
    {
        return CONTENTLOCATION_UNAPPROVED_FIELDNAME + NUMERIC_FIELD_POSTFIX;
    }

    public static String getCategoryKeyQueryFieldName()
    {
        return CATEGORY_FIELD_PREFIX + "key" + NUMERIC_FIELD_POSTFIX;
    }

    public static String getContentKeyQueryFieldName()
    {
        return CONTENTKEY + NUMERIC_FIELD_POSTFIX;
    }

    public static String getContentTypeKeyQueryFieldName()
    {
        return CONTENT_TYPE_PREFIX + "key" + NUMERIC_FIELD_POSTFIX;
    }

}
