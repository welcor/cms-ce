package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetContentByCategoryConverter
    extends DataSourceMethodConverter
{
    public GetContentByCategoryConverter()
    {
        super( "getContentByCategory" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 9 && params.length != 14 )
        {
            return null;
        }

        if ( params.length == 14 )
        {
            /*
               public XMLDocument getContentByCategory(
               0 String query,
               1 int[] categories,
               2 boolean includeSubCategories,
               3 String orderBy,
               4 int index,
               5 int count,
               6 boolean titlesOnly,
               7 int childrenLevel,
               8 int parentLevel,
               9 int parentChildrenLevel,
               10 boolean relatedTitlesOnly,
               11 boolean includeTotalCount,
               12 boolean includeUserRights,
               13 int[] contentTypes );
            */

            // If includeSubCategories, use all, else 1
            int levels = Boolean.valueOf( params[2] ) ? Integer.MAX_VALUE : 1;
            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[10] ) || !Boolean.valueOf( params[6] );

            return method().param( "categoryKeys", params[1] ).param( "levels", params[levels] ).param( "query", params[0] ).param(
                "orderBy", params[3] ).param( "index", params[4] ).param( "count", params[5] ).param( "includeData", Boolean.toString(
                includeData ) ).param( "childrenLevel", params[7] ).param( "parentLevel", params[8] ).build();
        }

        return method().params( params, "categoryKeys", "levels", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                                "parentLevel" ).build();
    }
}
