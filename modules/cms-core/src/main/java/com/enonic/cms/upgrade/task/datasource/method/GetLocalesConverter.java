/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetLocalesConverter
    extends DataSourceMethodConverter
{
    public GetLocalesConverter()
    {
        super( "getLocales" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 0 )
        {
            return null;
        }

        return method().build();
    }
}
