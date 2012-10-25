package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetSuperCategoryNamesConverter
    extends DataSourceMethodConverter
{
    public GetSuperCategoryNamesConverter()
    {
        super( "getSuperCategoryNames" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( !checkMinMax( params, 3, 3 ) )
        {
            return null;
        }

        return method().params( params, "categoryKey", "includeContentCount", "includeCurrent" ).build();
    }
}
