/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetRandomContentBySectionConverter
    extends DataSourceMethodConverter
{
    public GetRandomContentBySectionConverter()
    {
        super( "getRandomContentBySection" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 7 )
        {
            return null;
        }

        return method().params( params, "menuItemKeys", "levels", "query", "count", "includeData", "childrenLevel", "parentLevel" ).build();
    }
}
