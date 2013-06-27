/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetContentByQueryConverter
    extends DataSourceMethodConverter
{
    public GetContentByQueryConverter()
    {
        super( "getContentByQuery" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 7 )
        {
            return null;
        }

        return method().params( params, "query", "orderBy", "index", "count", "includeData", "childrenLevel", "parentLevel" ).build();
    }
}
