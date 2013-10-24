/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.el;

/**
 * This class implements the expression functions.
 */
public class StaticExpressionFunctions
{

    public static String isnotblank( Object str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isnotblank( toString( str ) ) );
    }

    public static String isblank( Object str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isblank( toString( str ) ) );
    }

    public static String isnotempty( Object str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isnotempty( toString( str ) ) );
    }

    public static String isempty( Object str )
    {
        return Boolean.toString( ExpressionFunctionsFactory.get().createExpressionFunctions().isempty( toString( str ) ) );
    }

    public static String select( Object s1, Object s2 )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().select( toString( s1 ), toString( s2 ) );
    }

    public static String concat( Object... str )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().concat( str );
    }

    public static String replace( Object source, Object regex, Object replacement )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().replace( toString( source ), toString( regex ),
                                                                                     toString( replacement ) );
    }

    public static String substring( Object source, int beginIndex, int endIndex )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().substring( toString( source ), beginIndex, endIndex );
    }

    public static int stringlength( Object source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().stringlength( toString( source ) );
    }

    public static String lower( Object source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().lower( toString( source ) );
    }

    public static String upper( Object source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().upper( toString( source ) );
    }

    public static String trim( Object source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().trim( toString( source ) );
    }

    public static int min( int v1, int v2 )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().min( v1, v2 );
    }

    public static int max( int v1, int v2 )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().max( v1, v2 );
    }

    public static String currentDate( Object format )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().currentDate( toString( format ) );
    }

    public static String currentDatePlusOffset( Object format, Object period )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().currentDatePlusOffset( toString( format ), toString( period ) );
    }

    public static String currentDateMinusOffset( Object format, Object period )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().currentDateMinusOffset( toString( format ),
                                                                                                    toString( period ) );
    }

    public static String periodHoursMinutes( int hours, int minutes )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().periodHoursMinutes( hours, minutes );
    }

    public static String pref( Object scope, Object key )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().pref( toString( scope ), toString( key ) );
    }

    /**
     * This method will take a freetext search string and create a valid query that can be used in the getContent* methods.  The search
     * string is spilt into tokens.  Using the operator, it may be specified whether the field must contain all or any of the words in the
     * search string.
     *
     * @param fieldName    The name of the field to search for the words in the search string.
     * @param searchString The words to search for.
     * @param operator     Must be either AND or OR.  Case doesn't matter.
     * @return A syntactically correct search that may be used as the query parameter in getContent* methods on the data source. With care,
     *         it may also be merged with other queries using AND or OR.
     * @throws IllegalArgumentException If any of the parameters are empty or the operator is not AND or OR.
     */
    public static String buildFreetextQuery( Object fieldName, Object searchString, Object operator )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().buildFreetextQuery( toString( fieldName ),
                                                                                                toString( searchString ),
                                                                                                toString( operator ) );
    }

    public static String getPageKey()
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().getPageKey();
    }

    public static String getPortletWindowKey()
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().getWindowKey();
    }

    public static String getPageKeyByPath( Object path )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().getPageKeyByPath( toString( path ) );
    }

    public static String getPageKeysByPath( Object path, Object predicate )
    {
        if ( !"child".equals( predicate ) )
        {
            throw new RuntimeException( "Only 'child' predicate is supported." );
        }

        return ExpressionFunctionsFactory.get().createExpressionFunctions().getPageKeysByPath( toString( path ) );
    }

    public static String urlEncode( Object source )
    {
        return ExpressionFunctionsFactory.get().createExpressionFunctions().urlEncode( toString( source ) );
    }

    private static String toString( final Object source )
    {
        return source == null ? null : source.toString();
    }
}
