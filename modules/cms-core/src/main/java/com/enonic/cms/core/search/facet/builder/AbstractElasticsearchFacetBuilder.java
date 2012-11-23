package com.enonic.cms.core.search.facet.builder;

import java.util.regex.Pattern;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.query.QueryField;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;

abstract class AbstractElasticsearchFacetBuilder
{
    protected String createQueryFieldName( final String field )
    {
        return QueryFieldNameResolver.resolveQueryFieldName( field );
    }

    protected String getDateFieldName( String fieldName )
    {
        QueryField queryField = new QueryField( createQueryFieldName( fieldName ) );
        return queryField.getFieldNameForDateQueries();
    }

    protected String getNumericFieldName( String fieldName )
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


}
