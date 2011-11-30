package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.search.IndexType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/9/11
 * Time: 2:12 PM
 */
public class QueryPathCreator
{

    public static QueryPath createQueryPath( String path )
    {
        QueryPath queryPath = new QueryPath( path );

        if ( StringUtils.startsWith( path, "data" ) )
        {
            queryPath.setRenderAsHasChildQuery( true ).setIndexType( IndexType.Customdata );
        }
        else if ( StringUtils.startsWith( path, "attachment" ) )
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
