/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetCategoriesConverter
    extends DataSourceMethodConverter
{
    public GetCategoriesConverter()
    {
        super( "getCategories" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 4 && params.length != 6 )
        {
            return null;
        }

        if ( params.length == 6 )
        {
            /*
              public XMLDocument getCategories(6)
              0  int key,
              1  int levels,
              2  boolean topLevel,
              3  boolean details, (skipped)
              4  boolean catCount, (skipped)
              5  boolean contentCount );
             */

            return method()
                .param( "categoryKey", params[0] )
                .param("levels", params[1])
                .param( "includeContentCount", params[5] )
                .param( "includeTopCategory", params[2] )
                .build();
        }

        return method().params( params, "categoryKey", "levels", "includeContentCount", "includeTopCategory" ).build();
    }
}
