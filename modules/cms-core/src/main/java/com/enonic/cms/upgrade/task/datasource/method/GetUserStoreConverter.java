/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetUserStoreConverter
    extends DataSourceMethodConverter
{
    public GetUserStoreConverter()
    {
        super( "getUserstore" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 1 )
        {
            return null;
        }

        return method( "getUserStore" ).params( params, "name" ).build();
    }
}
