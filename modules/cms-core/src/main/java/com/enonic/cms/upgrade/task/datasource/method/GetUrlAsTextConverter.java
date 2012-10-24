package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetUrlAsTextConverter
    extends DataSourceMethodConverter
{
    public GetUrlAsTextConverter()
    {
        super( "getURLAsText" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 2, 3 ) )
        {
            return null;
        }

        return method( "getUrlAsText" ).params( params, "url", "encoding", "timeout" ).build();
    }
}
