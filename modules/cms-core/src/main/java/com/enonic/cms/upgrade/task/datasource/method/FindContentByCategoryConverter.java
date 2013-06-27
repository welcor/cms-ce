/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jdom.Element;

import com.google.common.base.Strings;

final class FindContentByCategoryConverter
    extends DataSourceMethodConverter
{
    public FindContentByCategoryConverter()
    {
        super( "findContentByCategory" );
    }

    @Override
    public Element convert( final String[] params )
    {

        final int numberOfParams = params.length;

        if ( numberOfParams != 15 )
        {
            return null;
        }

        /*
       // Try to convert to getContentByCategory
       public XMLDocument findContentByCategory(15)
       0  String c,   QUERY
       1  String operator, QUERY
       2  int[] categories, OK
       3  boolean includeSubCategories, LEVELS
       4  String orderBy, OK
       5  int index, OK
       6  int count, OK
       7  boolean titlesOnly, INCLUDE-DATA
       8  int childrenLevel, OK
       9  int parentLevel, OK
       10 int parentChildrenLevel, SKIP
       11 boolean relatedTitlesOnly, INCLUDE-DATA
       12 boolean includeTotalCount, SKIP
       13 boolean includeUserRights, SKIP
       14 int[] contentTypes ); QUERY
        */

        final String operator = params[1];
        final boolean opAnd = ( operator != null ) && "AND".equalsIgnoreCase( operator );
        String query = convertSimpleSearch( params[0], opAnd );

        String contentTypesString = params[14];

        if ( !Strings.isNullOrEmpty( contentTypesString ) )
        {
            query = appendInFilterToQuery( query, contentTypesString, "contenttypekey" );
        }

        final Integer levels = Boolean.valueOf( params[3] ) ? Integer.MAX_VALUE : 1;

        // Translated from !relatedTitlesOnly && !titlesOnly
        final Boolean includeData = !Boolean.valueOf( params[11] ) || !Boolean.valueOf( params[7] );

        return method("getContentByCategory")
            .param( "categoryKeys", params[2] )
            .param( "levels", levels.toString() )
            .param( "query", query )
            .param( "orderBy", params[4] )
            .param( "index", params[5] )
            .param( "count", params[6] )
            .param( "includeData", Boolean.toString( includeData ) )
            .param( "childrenLevel", params[8] )
            .param( "parentLevel", params[9] )
            .build();
    }

    private String convertSimpleSearch( String search, boolean opAnd )
    {
        String operator = opAnd ? " AND " : " OR ";
        final StringBuilder query = new StringBuilder();

        if ( search != null )
        {
            HashSet<String> params = new HashSet<String>();
            StringTokenizer tok = new StringTokenizer( search, " " );

            while ( tok.hasMoreTokens() )
            {
                String param = tok.nextToken();
                if ( param.length() > 0 )
                {
                    params.add( param );
                }
            }

            for ( Iterator<String> i = params.iterator(); i.hasNext(); )
            {
                String param = i.next();
                query.append( "((title CONTAINS \"" ).append( param ).append( "\") OR " );
                query.append( "(data/* CONTAINS \"" ).append( param ).append( "\"))" );
                if ( i.hasNext() )
                {
                    query.append( operator );
                }
            }
        }

        return query.toString();
    }


}

