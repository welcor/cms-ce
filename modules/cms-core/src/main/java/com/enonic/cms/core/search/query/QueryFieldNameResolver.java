package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.search.IndexFieldnameNormalizer;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;


public class QueryFieldNameResolver
    extends IndexFieldNameConstants
{
    public static String resolveQueryFieldName( final String name )
    {
        return doNormalizeQueryFieldName( name );
    }

    public static String resolveQueryFieldName( final FieldExpr expression )
    {
        return doNormalizeQueryFieldName( expression.getPath() );
    }

    public static String resolveOrderFieldName( final FieldExpr expression )
    {
        return doNormalizeQueryFieldName( expression.getPath() ) + "." + ORDERBY_FIELDNAME_POSTFIX;
    }

    private static String doNormalizeQueryFieldName( final String fieldName )
    {
        String normalizedFieldName = IndexFieldnameNormalizer.normalizeFieldName( fieldName );

        if ( StringUtils.startsWith( normalizedFieldName, CONTENTDATA_PREFIX_ALIAS_FOR_BW_COMPATABILITY ) )
        {
            normalizedFieldName =
                StringUtils.replaceOnce( normalizedFieldName, CONTENTDATA_PREFIX_ALIAS_FOR_BW_COMPATABILITY, CONTENTDATA_PREFIX );
        }

        if ( StringUtils.startsWith( normalizedFieldName, ATTACHMENT_ALIAS_FOR_BW_COMPATABILITY ) )
        {
            normalizedFieldName =
                StringUtils.replaceOnce( normalizedFieldName, ATTACHMENT_ALIAS_FOR_BW_COMPATABILITY, ATTACHMENT_FIELDNAME );
        }

        return normalizedFieldName;
    }

    public static String getCategoryAccessTypeFieldName( final CategoryAccessType type )
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
