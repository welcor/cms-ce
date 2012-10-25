package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetMenuBranchConverter
    extends DataSourceMethodConverter
{
    public GetMenuBranchConverter()
    {
        super( "getMenuBranch" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 2, 4 ) )
        {
            return null;
        }

        if ( params.length == 3 )
        {
            return method().param( "menuItemKey", params[0] ).param( "includeTopLevel", params[1] ).build();
        }

        return method().params( params, "menuItemKey", "includeTopLevel", "startLevel", "levels" ).build();
    }
}
