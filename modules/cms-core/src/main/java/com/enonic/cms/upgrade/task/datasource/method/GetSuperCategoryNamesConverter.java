/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetSuperCategoryNamesConverter
    extends DataSourceMethodConverter
{
    public GetSuperCategoryNamesConverter()
    {
        super( "getSuperCategoryNames" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 3 )
        {
            return null;
        }

        return method().params( params, "categoryKey", "includeContentCount", "includeCurrent" ).build();
    }
}
