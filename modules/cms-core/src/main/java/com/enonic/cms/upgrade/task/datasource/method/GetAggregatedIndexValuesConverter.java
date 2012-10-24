package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetAggregatedIndexValuesConverter
    extends DataSourceMethodConverter
{
    public GetAggregatedIndexValuesConverter()
    {
        super( "getAggregatedIndexValues" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 4 )
        {
            return null;
        }

        return method().params( params, "field", "categoryKeys", "recursive", "contentTypeKeys" ).build();
    }
}
