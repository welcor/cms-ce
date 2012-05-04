package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class QueryFieldFactory
    extends IndexFieldNameConstants
{
    public static QueryField resolveQueryField( final String field )
    {
        QueryField queryField = new QueryField( field );

        detectContentKeyField( field, queryField );

        detectAttachmentField( field, queryField );

        return queryField;
    }

    private static void detectContentKeyField( final String field, final QueryField queryField )
    {
        if ( StringUtils.equals( field, CONTENTKEY_FIELDNAME ) )
        {
            queryField.setRenderAsIdQuery( true );
        }
    }

    private static void detectAttachmentField( final String field, final QueryField queryField )
    {
        if ( StringUtils.startsWith( field, ATTACHMENT_FIELDNAME ) )
        {
            queryField.setRenderAsHasChildQuery( true ).setIndexType( IndexType.Binaries );
        }
        else
        {
            queryField.setIndexType( IndexType.Content );
        }
    }

}
