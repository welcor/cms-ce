package com.enonic.cms.core.content.index.translator;


import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.TranslatedQuery;
import com.enonic.cms.core.content.index.queryexpression.QueryParserException;

import static org.junit.Assert.*;

public class ContentQueryTranslatorTest
{
    @Test
    public void value_is_converted_to_number_when_expression_with_integer_field_and_numeric_value_given_as_string()
    {
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator();
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "(status = '0')" );
        TranslatedQuery translatedQuery = contentQueryTranslator.translate( contentIndexQuery );

        StringBuilder expectedQuery = new StringBuilder();
        expectedQuery.append( "SELECT DISTINCT(x.contentKey)\n" );
        expectedQuery.append( "FROM com.enonic.cms.core.content.ContentIndexEntity AS x\n" );
        expectedQuery.append( "WHERE (x.contentStatus = 0.0)\n" );

        assertEquals( expectedQuery.toString().trim(), translatedQuery.getQuery().trim() );
    }

    @Test
    public void exception_is_thrown_when_expression_with_integer_field_and_decimal_value_given()
    {
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator();
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "(status = 2.1)" );
        try
        {
            contentQueryTranslator.translate( contentIndexQuery );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof QueryParserException );
            assertEquals( "Expected integer on right side of expression: status = 2.1", e.getMessage() );
        }
    }

    @Test
    public void exception_is_thrown_when_expression_with_integer_field_and_non_numeric_value_given_as_string()
    {
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator();
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "(status = 'a')" );
        try
        {
            contentQueryTranslator.translate( contentIndexQuery );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof QueryParserException );
            assertEquals( "Expected integer on right side of expression: status = 'a'", e.getMessage() );
        }
    }

    @Test
    public void exception_is_thrown_when_expression_with_integer_field_and_non_numeric_value_given()
    {
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator();
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "(status = date('2008-12-01'))" );
        try
        {
            contentQueryTranslator.translate( contentIndexQuery );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof QueryParserException );
            assertTrue( e.getMessage().startsWith( "Expected integer on right side of expression: status = " ) );
        }
    }
}
