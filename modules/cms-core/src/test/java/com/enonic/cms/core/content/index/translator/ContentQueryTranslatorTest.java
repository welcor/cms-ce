package com.enonic.cms.core.content.index.translator;


import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.TranslatedQuery;
import com.enonic.cms.core.content.index.queryexpression.QueryParserException;
import com.enonic.cms.store.dao.ContentTypeDao;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

public class ContentQueryTranslatorTest
{
    private ContentTypeDao contentTypeDaoMock;

    @Before
    public void setUp()
    {
        contentTypeDaoMock = createMock( ContentTypeDao.class );
    }

    @Test
    public void value_is_converted_to_number_when_expression_with_integer_field_and_numeric_value_given_as_string()
    {
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator( null );
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "(status = '0')" );
        TranslatedQuery translatedQuery = contentQueryTranslator.translate( contentIndexQuery );

        StringBuilder expectedQuery = new StringBuilder();
        expectedQuery.append( "SELECT DISTINCT(x.contentKey)\n" );
        expectedQuery.append( "FROM com.enonic.cms.core.content.ContentIndexEntity AS x\n" );
        expectedQuery.append( "WHERE (x.contentStatus = 0.0)\n" );

        assertEquals( expectedQuery.toString().trim(), translatedQuery.getQuery().trim() );
    }

    @Test
    public void value_is_converted_to_contenttypekey_when_expression_with_contenttype_is_present()
    {
        ContentTypeEntity article = new ContentTypeEntity();
        article.setKey( 1001 );

        ContentTypeEntity document = new ContentTypeEntity();
        document.setKey( 1002 );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( article ).times( 1 );
        expect( contentTypeDaoMock.findByName( "document" ) ).andReturn( document ).times( 1 );
        replay( contentTypeDaoMock );

        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator( contentTypeDaoMock );
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "(contenttype = 'article') OR (contenttype = 'document')" );
        TranslatedQuery translatedQuery = contentQueryTranslator.translate( contentIndexQuery );

        StringBuilder expectedQuery = new StringBuilder();
        expectedQuery.append( "SELECT DISTINCT(x.contentKey)\n" );
        expectedQuery.append( "FROM com.enonic.cms.core.content.ContentIndexEntity AS x\n" );
        expectedQuery.append( "WHERE (( x.contentTypeKey = 1001.0 OR x.contentTypeKey = 1002.0 ))\n" );

        assertEquals( expectedQuery.toString().trim(), translatedQuery.getQuery().trim() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void value_is_converted_to_contenttypekey_IN_when_expression_with_contenttype_IN_is_present()
    {
        ContentTypeEntity article = new ContentTypeEntity();
        article.setKey( 1001 );

        ContentTypeEntity document = new ContentTypeEntity();
        document.setKey( 1002 );

        ContentTypeDao contentTypeDaoMock = createMock( ContentTypeDao.class );
        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( article );
        expect( contentTypeDaoMock.findByName( "document" ) ).andReturn( document );
        replay( contentTypeDaoMock );

        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator( contentTypeDaoMock );
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "contenttype IN ('article', 'document')" );
        TranslatedQuery translatedQuery = contentQueryTranslator.translate( contentIndexQuery );

        StringBuilder expectedQuery = new StringBuilder();
        expectedQuery.append( "SELECT DISTINCT(x.contentKey)\n" );
        expectedQuery.append( "FROM com.enonic.cms.core.content.ContentIndexEntity AS x\n" );
        expectedQuery.append( "WHERE (x.contentTypeKey IN (1001.0,1002.0))\n" );

        assertEquals( expectedQuery.toString().trim(), translatedQuery.getQuery().trim() );
        verify( contentTypeDaoMock );
    }

    @Test
    public void exception_is_thrown_when_expression_with_integer_field_and_decimal_value_given()
    {
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator( null );
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
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator( null );
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
        ContentQueryTranslator contentQueryTranslator = new ContentQueryTranslator( null );
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
