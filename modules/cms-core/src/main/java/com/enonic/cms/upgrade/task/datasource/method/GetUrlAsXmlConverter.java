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
        if ( params.length < 1 )
        {
            return null;
        }

        if ( params.length > 2 )
        {
            return null;
        }

        return method( "getUrlAsXml" ).params( params, "url", "timeout" ).build();
    }
}
