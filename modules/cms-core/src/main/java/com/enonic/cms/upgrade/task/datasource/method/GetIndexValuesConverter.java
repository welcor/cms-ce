package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetIndexValuesConverter
    extends DataSourceMethodConverter
{
    public GetIndexValuesConverter()
    {
        super( "getIndexValues" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 8 )
        {
            return null;
        }

        return method().params( params, "field", "categoryKeys", "recursive", "contentTypeKeys", "index", "count", "distinct",
                                "order" ).build();
    }
}
