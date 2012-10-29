package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

import com.google.common.base.Strings;

final class GetMyContentByCategoryConverter
    extends DataSourceMethodConverter
{
    public GetMyContentByCategoryConverter()
    {
        super( "getMyContentByCategory" );
    }

    /*
     public XMLDocument getMyContentByCategory( 14 )
     0  String query,
     1  int[] categories,
     2  boolean includeSubCategories,
     3  String orderBy,
     4  int index,
     5  int count,
     6  boolean titlesOnly,
     7  int childrenLevel,
     8  int parentLevel,
     9  int parentChildrenLevel,
     10  boolean relatedTitlesOnly,
     11  boolean includeTotalCount,
     12  boolean includeUserRights,
     13  int[] contentTypes );
     */

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 14 )
        {
            return null;
        }

        String query = params[0];

        String contentTypesString = params[13];

        if ( !Strings.isNullOrEmpty( contentTypesString ) )
        {
            query = appendInFilterToQuery( query, contentTypesString, "contenttypekey" );
        }

        final Integer levels = Boolean.valueOf( params[2] ) ? Integer.MAX_VALUE : 1;

        // Translated from !relatedTitlesOnly && !titlesOnly
        final Boolean includeData = !Boolean.valueOf( params[10] ) || !Boolean.valueOf( params[6] );

        return method("getContentByCategory")
            .param( "categoryKeys", params[1] )
            .param( "levels", levels.toString() )
            .param( "query", query )
            .param( "orderBy", params[3] )
            .param( "index", params[4] )
            .param( "count", params[5] )
            .param( "includeData", Boolean.toString( includeData ) )
            .param( "childrenLevel", params[7] )
            .param( "parentLevel", params[8] )
            .param( "filterOnUser", "true" )
            .build();
    }
}
