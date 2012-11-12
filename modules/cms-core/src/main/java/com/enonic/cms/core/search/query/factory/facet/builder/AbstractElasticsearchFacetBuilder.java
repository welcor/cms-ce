package com.enonic.cms.core.search.query.factory.facet.builder;

import java.util.regex.Pattern;

import org.elasticsearch.common.Strings;

abstract class AbstractElasticsearchFacetBuilder
{
    protected enum regExFlags
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

        private regExFlags( final int value )
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    protected static String[] getCommaDelimitedStringAsArray( String commaSeparatedString )
    {
        return Strings.commaDelimitedListToStringArray( commaSeparatedString );
    }


}
