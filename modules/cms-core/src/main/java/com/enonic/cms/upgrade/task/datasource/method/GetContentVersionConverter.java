package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetContentVersionConverter
    extends DataSourceMethodConverter
{
    public GetContentVersionConverter()
    {
        super( "getContentVersion" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 2 )
        {
            return null;
        }

        return method().params( params, "versionKeys", "childrenLevel" ).build();
    }
}
