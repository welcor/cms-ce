/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetPreferencesConverter
    extends DataSourceMethodConverter
{
    public GetPreferencesConverter()
    {
        super( "getPreferences" );
    }

    @Override
    public Element convert( final String[] params )
    {
        final MethodElementBuilder builder = method();

        if ( !checkMinMax( params, 0, 3 ) )
        {
            return null;
        }

        return builder.params( params, "scope", "keyPattern", "uniqueMatch" ).build();
    }
}
