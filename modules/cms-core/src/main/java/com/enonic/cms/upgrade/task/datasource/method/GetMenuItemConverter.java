package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetMenuItemConverter
    extends DataSourceMethodConverter
{
    public GetMenuItemConverter()
    {
        super( "getMenuItem" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 2, 3 ) )
        {
            return null;
        }

        return method().params( params, "menuItemKey", "withParents" ).build();
    }
}
