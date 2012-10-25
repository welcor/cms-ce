package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetRandomContentByCategoryConverter
    extends DataSourceMethodConverter
{
    public GetRandomContentByCategoryConverter()
    {
        super( "getRandomContentByCategory" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 7 )
        {
            return null;
        }

        return method().params( params, "categoryKeys", "levels", "query", "count", "includeData", "childrenLevel", "parentLevel" ).build();
    }
}
