/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.builder;

import java.util.regex.Pattern;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.model.FacetOrderBy;
import com.enonic.cms.core.search.query.QueryField;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;

abstract class AbstractElasticsearchFacetBuilder
{
    protected String createQueryFieldName( final String field )
    {
        return QueryFieldNameResolver.resolveQueryFieldName( field );
    }

    protected String createDateFieldName( String fieldName )
    {
        QueryField queryField = new QueryField( createQueryFieldName( fieldName ) );
        return queryField.getFieldNameForDateQueries();
    }

    protected String createNumericFieldName( String fieldName )
    {
        QueryField queryField = new QueryField( createQueryFieldName( fieldName ) );
        return queryField.getFieldNameForNumericQueries();
    }

    public enum RegExpFlags
    {
        CASE_INSENSITIVE( Pattern.CASE_INSENSITIVE ),
        MULTILINE( Pattern.MULTILINE ),
        DOTALL( Pattern.DOTALL ),
        UNICODE_CASE( Pattern.UNICODE_CASE ),
        CANON_EQ( Pattern.CANON_EQ ),
        UNIX_LINES( Pattern.UNIX_LINES ),
        LITERAL( Pattern.LITERAL ),
        COMMENTS( Pattern.COMMENTS );

        int value;

        private RegExpFlags( final int value )
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    protected static String[] getCommaDelimitedStringAsArraySkipWhitespaces( String commaSeparatedString )
    {
        if ( Strings.isNullOrEmpty( commaSeparatedString ) )
        {
            return null;
        }

        return commaSeparatedString.split( ",\\s*" );
    }

    protected String createElasticsearchOrderByString( FacetOrderBy facetOrderBy )
    {
        if ( facetOrderBy == null )
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        appendDirection( facetOrderBy, builder );

        String orderbyESStringValue = translateToEsSortValues( facetOrderBy );

        builder.append( orderbyESStringValue );

        return builder.toString();
    }

    private void appendDirection( final FacetOrderBy facetOrderBy, final StringBuilder builder )
    {
        if ( facetOrderBy.getDirection() != null && facetOrderBy.getDirection().equals( FacetOrderBy.Direction.ASC ) )
        {
            builder.append( "reverse_" );
        }
    }

    private String translateToEsSortValues( final FacetOrderBy facetOrderBy )
    {
        String orderbyESStringValue = "";

        final FacetOrderBy.Value value = facetOrderBy.getValue();
        switch ( value )
        {
            case HITS:
                orderbyESStringValue = "count";
                break;
            case SUM:
                orderbyESStringValue = "total";
                break;
            default:
                orderbyESStringValue = value.toString();
        }
        return orderbyESStringValue;
    }

}
