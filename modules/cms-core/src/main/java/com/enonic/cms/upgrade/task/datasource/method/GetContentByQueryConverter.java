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
        if ( !checkMinMax( params, 7, 7 ) )
        {
            return null;
        }

        return method().params( params, "query", "orderBy", "index", "count", "includeData", "childrenLevel", "parentLevel" ).build();
    }
}
