package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetMenuConverter
    extends DataSourceMethodConverter
{
    public GetMenuConverter()
    {
        super( "getMenu" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 2, 4 ) )
        {
            return null;
        }

        if ( params.length == 2 )
        {
            return method().params( params, "tagItem", "levels" ).build();
        }

        return method().params( params, "siteKey", "tagItem", "levels", "details" ).build();
    }
}
