package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetRelatedContentConverter
    extends DataSourceMethodConverter
{
    public GetRelatedContentConverter()
    {
        super( "getRelatedContent" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 9, 9 ) )
        {
            return null;
        }

        return method().params( params, "contentKeys", "relation", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                                "parentLevel" ).build();
    }
}
