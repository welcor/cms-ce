package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetContentBySectionConverter
    extends DataSourceMethodConverter
{
    public GetContentBySectionConverter()
    {
        super( "getContentBySection" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 9 && params.length != 13 && params.length != 14 )
        {
            return null;
        }

        if ( params.length == 13 )
        {
            /*
             public XMLDocument getContentBySection( 13)
             0 int[] menuItemKeys,
             1 int levels,
             2 String orderBy,
             3 int fromIndex,
             4 int count,
             5 boolean titlesOnly,
             6 int parentLevel,
             7 int childrenLevel,
             8 int parentChildrenLevel,
             9 boolean relatedTitlesOnly,
             10 boolean includeTotalCount,
             11 boolean includeUserRights,
             12 int[] filterByContentTypes )
            */

            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[9] ) || !Boolean.valueOf( params[5] );

            return method().param( "menuItemKeys", params[0] ).param( "levels", params[1] ).param( "query", "" ).param( "orderBy",
                                                                                                                        params[2] ).param(
                "index", params[3] ).param( "count", params[4] ).param( "includeData", Boolean.toString( includeData ) ).param(
                "childrenLevel", params[7] ).param( "parentLevel", params[6] ).build();
        }

        if ( params.length == 14 )
        {

            /*
               public XMLDocument getContentBySection(14)
               0 String query,
               1 int[] menuItemKeys,
               2 int levels,
               3 String orderBy,
               4 int fromIndex,
               5 int count,
               6 boolean titlesOnly,
               7 int parentLevel,
               8 int childrenLevel,
               9 int parentChildrenLevel,
               10 boolean relatedTitlesOnly,
               11 boolean includeTotalCount,
               12 boolean includeUserRights,
               13 int[] filterByContentType );
            */

            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[10] ) || !Boolean.valueOf( params[6] );

            return method().param( "menuItemKeys", params[1] ).param( "levels", params[2] ).param( "query", params[0] ).param( "orderBy",
                                                                                                                               params[3] ).param(
                "index", params[4] ).param( "count", params[5] ).param( "includeData", Boolean.toString( includeData ) ).param(
                "childrenLevel", params[8] ).param( "parentLevel", params[7] ).build();
        }

        return method().params( params, "menuItemKeys", "levels", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                                "parentLevel" ).build();
    }
}
