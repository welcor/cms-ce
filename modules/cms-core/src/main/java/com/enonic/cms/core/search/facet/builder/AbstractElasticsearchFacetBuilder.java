package com.enonic.cms.core.search.facet.builder;

import java.util.regex.Pattern;

import com.google.common.base.Strings;


abstract class AbstractElasticsearchFacetBuilder
{
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
