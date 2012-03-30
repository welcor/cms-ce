package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.category.CategoryAccessType;
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

    public static String resolveQueryFieldName( FieldExpr expression )
    {
        return doNormalizeQueryFieldName( expression.getPath() );
    }

    private static String doNormalizeQueryFieldName( String name )
    {
        String normalized = name.replace( '/', '.' ).replace( '.', '_' ).replaceAll( "@", "" ).toLowerCase();

        if ( StringUtils.startsWith( normalized, CONTENTDATA_ALIAS_FOR_BW_COMPATABILITY ) )
        {
            normalized = StringUtils.replaceOnce( normalized, CONTENTDATA_ALIAS_FOR_BW_COMPATABILITY, CUSTOMDATA_FIELDNAME );
        }

        if ( StringUtils.startsWith( normalized, ATTACHMENT_ALIAS_FOR_BW_COMPATABILITY ) )
        {
            normalized = StringUtils.replaceOnce( normalized, ATTACHMENT_ALIAS_FOR_BW_COMPATABILITY, ATTACHMENT_FIELDNAME );
        }

        return normalized;
    }

    /*   public static String getOrderByFieldName( FieldExpr expression )
     {
         return doGetOrderByFieldName( expression.getPath() );
     }

     public static String getOrderByFieldName( String fieldName )
     {
         return doGetOrderByFieldName( fieldName );
     }

     private static String doGetOrderByFieldName( String fieldName )
     {
         return ORDER_FIELD_PREFIX + doNormalizeQueryFieldName( fieldName );
     }
    */

    public static String getSectionKeysApprovedQueryFieldName()
    {
        return CONTENTLOCATION_APPROVED_FIELDNAME; //+ NUMERIC_FIELD_POSTFIX;
    }

    public static String getSectionKeysUnapprovedQueryFieldName()
    {
        return CONTENTLOCATION_UNAPPROVED_FIELDNAME; // + NUMERIC_FIELD_POSTFIX;
    }

    public static String getCategoryKeyQueryFieldName()
    {
        return CATEGORY_FIELD_PREFIX + "key"; // + NUMERIC_FIELD_POSTFIX;
    }

    public static String getContentKeyQueryFieldName()
    {
        return CONTENTKEY_FIELDNAME; // + NUMERIC_FIELD_POSTFIX;
    }

    public static String getContentTypeKeyQueryFieldName()
    {
        return CONTENT_TYPE_PREFIX + "key"; // + NUMERIC_FIELD_POSTFIX;
    }

    public static String getContentStatusQueryFieldName()
    {
        return "status";
    }

    public static String getCategoryAccessTypeFieldName()
    {
        return "categoryaccesstype";
    }


    public static String getCategoryAccessTypeFieldName( CategoryAccessType type )
    {
        switch ( type )
        {
            case READ:
                return CONTENT_ACCESS_READ_FIELDNAME;
            case ADMIN_BROWSE:
                return CONTENT_CATEGORY_ACCESS_BROWSE_FIELDNAME;
            case APPROVE:
                return CONTENT_CATEGORY_ACCESS_APPROVE_FIELDNAME;
            case CREATE:
                return CONTENT_ACCESS_UPDATE_FIELDNAME;
            case ADMINISTRATE:
                return CONTENT_CATEGORY_ACCESS_ADMINISTRATE_FIELDNAME;
        }
        throw new UnsupportedOperationException( "Unexpected CategoryAccessType: " + type.name() );
    }
}
