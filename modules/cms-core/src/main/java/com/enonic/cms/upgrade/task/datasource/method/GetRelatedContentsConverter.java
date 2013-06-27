/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import java.util.List;

import org.jdom.Element;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

final class GetRelatedContentsConverter
    extends DataSourceMethodConverter
{
    public GetRelatedContentsConverter()
    {
        super( "getRelatedContents" );
    }


    @Override
    public Element convert( final String[] params )
    {
        List<Integer> validParametersNumber = Lists.newArrayList( 13, 15, 16 );

        final int parametersLength = params.length;

        if ( !validParametersNumber.contains( parametersLength ) )
        {
            return null;
        }

        if ( parametersLength == 13 )
        {
            String query = "";

            final String categoriesString = params[10];
            if ( !Strings.isNullOrEmpty( categoriesString ) )
            {
                query = appendInFilterToQuery( query, categoriesString, "categorykey" );
            }

            final String contentTypesString = params[12];
            if ( !Strings.isNullOrEmpty( contentTypesString ) )
            {
                query = appendInFilterToQuery( query, contentTypesString, "contenttypekey" );
            }

            return method("getRelatedContent")
                .param( "contentKeys", params[1])
                .param( "relation", params[0])
                .param( "orderBy", params[2])
                .param( "index", params[4])
                .param( "count", params[5])
                .param( "includeData", "true" )
                .param( "childrenLevel", params[7])
                .param( "parentLevel", params[6])
                .param( "requireAll", params[3])
                .param( "query", query )
                .build();

        }

        if ( parametersLength == 15 )
        {
            /*
                 public XMLDocument getRelatedContents_old(15)
                    0   int relation,
                    1   int[] contentKeys,
                    2   String orderBy,
                    3   boolean requireAll,
                    4   int fromIndex,
                    5   int count,
                    6   boolean titlesOnly,
                    7   int parentLevel,
                    8   int childrenLevel,
                    9   int parentChildrenLevel,
                    10   boolean relatedTitlesOnly,
                    11   boolean includeTotalCount,
                    12   int[] filterByCategories,
                    13   boolean categoryRecursive,
                    14   int[] filterByContentTypes );
             */

            String query = "";

            final String categoriesString = params[12];
            if ( !Strings.isNullOrEmpty( categoriesString ) )
            {
                query = appendInFilterToQuery( query, categoriesString, "categorykey" );
            }

            final String contentTypesString = params[14];
            if ( !Strings.isNullOrEmpty( contentTypesString ) )
            {
                query = appendInFilterToQuery( query, contentTypesString, "contenttypekey" );
            }

            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[10] ) || !Boolean.valueOf( params[6] );

            return method("getRelatedContent")
                .param( "contentKeys", params[1])
                .param( "relation", params[0])
                .param( "orderBy", params[2])
                .param( "index", params[4])
                .param( "count", params[5])
                .param( "includeData", Boolean.toString( includeData ) )
                .param( "childrenLevel", params[8])
                .param( "parentLevel", params[7])
                .param( "requireAll", params[3])
                .param( "query", query )
                .build();
            }

            /*
              public XMLDocument getRelatedContents_old(16)
                 0   int relation,
                 1   int[] contentKeys,
                 2   String query,
                 3   String orderBy,
                 4   boolean requireAll,
                 5   int fromIndex,
                 6   int count,
                 7   boolean titlesOnly,
                 8   int parentLevel,
                 9   int childrenLevel,
                 10   int parentChildrenLevel,
                 11   boolean relatedTitlesOnly,
                 12   boolean includeTotalCount,
                 13   int[] filterByCategories,
                 14   boolean categoryRecursive,
                 15   int[] filterByContentTypes );
             */

            String query = params[2];

            final String categoriesString = params[13];
            if ( !Strings.isNullOrEmpty( categoriesString ) )
            {
                query = appendInFilterToQuery( query, categoriesString, "categorykey" );
            }

            final String contentTypesString = params[15];
            if ( !Strings.isNullOrEmpty( contentTypesString ) )
            {
                query = appendInFilterToQuery( query, contentTypesString, "contenttypekey" );
            }

            // Translated from !relatedTitlesOnly && !titlesOnly
            boolean includeData = !Boolean.valueOf( params[11] ) || !Boolean.valueOf( params[7] );

            return method("getRelatedContent")
                .param( "contentKeys", params[1])
                .param( "relation", params[0])
                .param( "query", query)
                .param( "orderBy", params[3])
                .param( "index", params[5])
                .param( "count", params[6])
                .param( "includeData", Boolean.toString( includeData) )
                .param( "childrenLevel", params[9])
                .param( "parentLevel", params[8])
                .param( "requireAll", params[4])
                .build();
    }

}
