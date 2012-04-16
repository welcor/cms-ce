package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/9/11
 * Time: 2:12 PM
 */
public class QueryPathResolver
    extends IndexFieldNameConstants
{
    public static QueryField resolveQueryPath( String path )
    {
        QueryField queryField = new QueryField( path );

        detectContentKeyPath( path, queryField );

        detectAttachmentPath( path, queryField );

        detectedCustomDataField( path, queryField );

        return queryField;
    }

    private static void detectAttachmentPath( final String path, final QueryField queryField )
    {
        if ( StringUtils.startsWith( path, ATTACHMENT_FIELDNAME ) )
        {
            queryField.setRenderAsHasChildQuery( true ).setIndexType( IndexType.Binaries );
        }
        else
        {
            queryField.setIndexType( IndexType.Content );
        }
    }

    private static void detectContentKeyPath( final String path, final QueryField queryField )
    {
        if ( StringUtils.equals( path, CONTENTKEY_FIELDNAME ) )
        {
            queryField.setRenderAsIdQuery( true );
        }
    }

    private static void detectedCustomDataField( String path, final QueryField queryField )
    {
        if ( path.startsWith( CONTENTDATA_PREFIX ) || path.startsWith( CONTENTDATA_PREFIX_ALIAS_FOR_BW_COMPATABILITY ) )
        {
            queryField.setIsCustomDataField( true );
        }
    }
}
