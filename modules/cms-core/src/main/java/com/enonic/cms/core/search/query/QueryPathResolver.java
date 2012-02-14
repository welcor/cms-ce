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
    public static QueryPath resolveQueryPath( String path )
    {
        QueryPath queryPath = new QueryPath( path );

        if ( StringUtils.equals( path, CONTENTKEY_FIELDNAME ) )
        {
            queryPath.setRenderAsIdQuery( true );
        }

        if ( StringUtils.startsWith( path, ATTACHMENT_FIELDNAME ) )
        {
            queryPath.setRenderAsHasChildQuery( true ).setIndexType( IndexType.Binaries );
        }
        else
        {
            queryPath.setIndexType( IndexType.Content );
        }

        return queryPath;
    }


}
