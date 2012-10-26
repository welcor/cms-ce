package com.enonic.cms.upgrade.task.datasource.method;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;

final class GetContentConverter
    extends DataSourceMethodConverter
{
    public GetContentConverter()
    {
        super( "getContent" );
    }

    @Override
    public Element convert( final String[] params )
    {
        List<Integer> validLengths = Lists.newArrayList( 5, 6, 8, 9, 10 );

        final int numberOfParams = params.length;

        if ( !validLengths.contains( numberOfParams ) )
        {
            return null;
        }

        if ( numberOfParams == 5 )
        {
            /*
             public XMLDocument getContent(5)
                0 int[] contentKey,
                1 int parentLevel,
                2 int childrenLevel,
                3 int parentChildrenLevel,
                4 boolean updateStatistics );
             */

            return method().param( "contentKeys", params[0] ).param( "includeData", "true" ).param( "childrenLevel", params[2] ).param(
                "parentLevel", params[1] ).build();
        }

        if ( numberOfParams == 6 )
        {
            /*
              public XMLDocument getContent(6)
                0 int[] contentKey,
                1 int parentLevel,
                2 int childrenLevel,
                3 int parentChildrenLevel,
                4 boolean updateStatistics,
                5 boolean includeUserRights );
             */

            return method().param( "contentKeys", params[0] ).param( "includeData", "true" ).param( "childrenLevel", params[2] ).param(
                "parentLevel", params[1] ).build();
        }

        if ( numberOfParams == 9 )
        {
            /*
              public XMLDocument getContent(9)
                0 int[] contentKey,
                1 int parentLevel,
                2 int childrenLevel,
                3 int parentChildrenLevel,
                4 boolean updateStatistics,
                5 boolean includeUserRights,
                6 int[] filterByCategories,
                7 boolean categoryRecursive,
                8 int[] filterByContentTypes );

             */

            return method().param( "contentKeys", params[0] ).param( "includeData", "true" ).param( "childrenLevel", params[2] ).param(
                "parentLevel", params[1] ).build();
        }

        if ( numberOfParams == 10 )
        {
            /*
               public XMLDocument getContent(10)
               0 int[] contentKey,
               1 int parentLevel,
               2 int childrenLevel,
               3 int parentChildrenLevel,
               4 boolean updateStatistics,
               5 boolean relatedTitlesOnly,
               6 boolean includeUserRights,
               7 int[] filterByCategories,
               8 boolean categoryRecursive,
               9 int[] filterByContentTypes );
             */

            // Extract from !relatedTitlesOnly
            Boolean includeData = !Boolean.valueOf( params[5] );

            return method().param( "contentKeys", params[0] ).param( "includeData", Boolean.toString( includeData ) ).param(
                "childrenLevel", params[2] ).param( "parentLevel", params[1] ).build();
        }

        return method().params( params, "contentKeys", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                                "parentLevel" ).build();
    }
}

