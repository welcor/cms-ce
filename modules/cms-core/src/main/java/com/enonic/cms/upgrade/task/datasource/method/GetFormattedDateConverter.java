package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetFormattedDateConverter
    extends DataSourceMethodConverter
{
    public GetFormattedDateConverter()
    {
        super( "getFormattedDate" );
    }

    @Override
    public Element convert( final String[] params )
    {
        final MethodElementBuilder builder = method();

        if ( params.length != 4 )
        {
            return null;
        }

        return builder.params( params, "offset", "dateFormat", "language", "country" ).build();
    }
}
