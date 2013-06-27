/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetUrlAsXmlConverter
    extends DataSourceMethodConverter
{
    public GetUrlAsXmlConverter()
    {
        super( "getURLAsXML" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 1, 2 ) )
        {
            return null;
        }

        return method( "getUrlAsXml" ).params( params, "url", "timeout" ).build();
    }
}
