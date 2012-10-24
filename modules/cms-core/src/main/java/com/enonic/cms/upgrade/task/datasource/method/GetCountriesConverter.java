package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetCountriesConverter
    extends DataSourceMethodConverter
{
    public GetCountriesConverter()
    {
        super( "getCountries" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 2 )
        {
            return null;
        }

        return method().params( params, "countryCodes", "includeRegions" ).build();
    }
}
