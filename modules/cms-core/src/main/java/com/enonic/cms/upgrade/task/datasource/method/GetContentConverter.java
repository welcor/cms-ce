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

    /*

    public XMLDocument getContent(8) ----> MAIN
    int[] contentKeys,
    String query,
    String orderBy,
    int index,
    int count,
    boolean includeData,
    int childrenLevel,
    int parentLevel );


    public XMLDocument getContent(10)
    int[] contentKey,
    int parentLevel,
    int childrenLevel,
    int parentChildrenLevel,
    boolean updateStatistics,
    boolean relatedTitlesOnly,
    boolean includeUserRights,
    int[] filterByCategories,
    boolean categoryRecursive,
    int[] filterByContentTypes );
     */


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

            return method().param( "contentKeys", params[0] ).param( "query", "" ).param( "orderBy", "" ).param( "index", "0" ).param(
                "count", "-1" ).param( "includeData", "true" ).param( "childrenLevel", params[2] ).param( "parentLevel",
                                                                                                          params[1] ).build();
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

            return method().param( "contentKeys", params[0] ).param( "query", "" ).param( "orderBy", "" ).param( "index", "0" ).param(
                "count", "-1" ).param( "includeData", "true" ).param( "childrenLevel", params[2] ).param( "parentLevel",
                                                                                                          params[1] ).build();
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

            return method().param( "contentKeys", params[0] ).param( "query", "" ).param( "orderBy", "" ).param( "index", "0" ).param(
                "count", "-1" ).param( "includeData", "true" ).param( "childrenLevel", params[2] ).param( "parentLevel",
                                                                                                          params[1] ).build();

        }

        {
            return method().params( params, "contentKeys", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                                    "parentLevel" ).build();
        }
    }

    /*

      .param( "contentKeys", params[x] )
               .param( "query", params[x] )
               .param( "orderBy", params[x] )
               .param( "index", params[x] )
               .param( "count", params[x] )
               .param( "includeData", params[x] )
               .param( "childrenLevel", params[x] )
               .param( "parentLevel", params[x] )
               .build();

    */

}

