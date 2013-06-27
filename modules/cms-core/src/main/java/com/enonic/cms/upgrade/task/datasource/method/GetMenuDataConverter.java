/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetMenuDataConverter
    extends DataSourceMethodConverter
{
    public GetMenuDataConverter()
    {
        super( "getMenuData" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 0, 1 ) )
        {
            return null;
        }

        return method().params( params, "siteKey" ).build();
    }
}
