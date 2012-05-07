package com.enonic.cms.core.content.index.queryexpression;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.index.ContentIndexConstants;
import com.enonic.cms.store.dao.ContentTypeDao;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

public class ContentTypeEvaluatorTest
{
    private static final String CONTENT_TYPE = ContentIndexConstants.F_CONTENT_TYPE_NAME;

    private ContentTypeDao contentTypeDaoMock;

    @Before
    public void setUp()
    {
        contentTypeDaoMock = createMock( ContentTypeDao.class );
    }

    @Test
    public void evaluate_does_not_change_value_when_left_is_not_an_contenttype()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );
        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "fullText" ), new ValueExpr( "article" ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( "fullText", ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isString() );
        assertEquals( "article", ( (ValueExpr) compareExpr.getRight() ).getValue() );

        assertEquals( CompareExpr.EQ, compareExpr.getOperator() );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_not_EQUAL_NOT_EQUAL_IN_NOT_IN()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );
        CompareExpr compareExpr = (CompareExpr) evaluator.evaluate(
            new CompareExpr( CompareExpr.LIKE, new FieldExpr( CONTENT_TYPE ), new ValueExpr( "article" ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isString() );
        assertEquals( "article", ( (ValueExpr) compareExpr.getRight() ).getValue() );

        assertEquals( CompareExpr.LIKE, compareExpr.getOperator() );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_EQUAL_but_right_is_not_value_expression()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        OrderFieldExpr[] orderBy = new OrderFieldExpr[0];

        CompareExpr compareExpr = (CompareExpr) evaluator.evaluate(
            new CompareExpr( CompareExpr.EQ, new FieldExpr( CONTENT_TYPE ), new OrderByExpr( orderBy ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof OrderByExpr );
        assertArrayEquals( orderBy, ( (OrderByExpr) compareExpr.getRight() ).getFields() );

        assertEquals( CompareExpr.EQ, compareExpr.getOperator() );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_EQUAL_but_value_is_not_string()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );
        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( CONTENT_TYPE ), new ValueExpr( 100.0d ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isNumber() );
        assertEquals( 100.0d, ( (ValueExpr) compareExpr.getRight() ).getValue() );

        assertEquals( CompareExpr.EQ, compareExpr.getOperator() );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_EQUAL_but_value_is_not_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( null ).times( 1 );
        replay( contentTypeDaoMock );

        CompareExpr compareExpr = (CompareExpr) evaluator.evaluate(
            new CompareExpr( CompareExpr.EQ, new FieldExpr( CONTENT_TYPE ), new ValueExpr( "article" ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isString() );
        assertEquals( "article", ( (ValueExpr) compareExpr.getRight() ).getValue() );

        assertEquals( CompareExpr.EQ, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void evaluate_changes_value_when_operation_is_EQUAL_and_value_is_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        ContentTypeEntity type = new ContentTypeEntity();
        type.setKey( 99 );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( type ).times( 1 );
        replay( contentTypeDaoMock );

        CompareExpr compareExpr = (CompareExpr) evaluator.evaluate(
            new CompareExpr( CompareExpr.EQ, new FieldExpr( CONTENT_TYPE ), new ValueExpr( "article" ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( "contenttypekey", ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isNumber() );
        assertEquals( 99.0f, ( (ValueExpr) compareExpr.getRight() ).getValue() );

        assertEquals( CompareExpr.EQ, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void evaluate_changes_value_when_operation_is_NOT_EQUAL_and_value_is_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        ContentTypeEntity type = new ContentTypeEntity();
        type.setKey( 99 );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( type ).times( 1 );
        replay( contentTypeDaoMock );

        CompareExpr compareExpr = (CompareExpr) evaluator.evaluate(
            new CompareExpr( CompareExpr.NEQ, new FieldExpr( CONTENT_TYPE ), new ValueExpr( "article" ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( "contenttypekey", ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isNumber() );
        assertEquals( 99.0f, ( (ValueExpr) compareExpr.getRight() ).getValue() );

        assertEquals( CompareExpr.NEQ, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_IN_but_right_is_not_array_expression()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        OrderFieldExpr[] orderBy = new OrderFieldExpr[0];

        CompareExpr compareExpr = (CompareExpr) evaluator.evaluate(
            new CompareExpr( CompareExpr.IN, new FieldExpr( CONTENT_TYPE ), new OrderByExpr( orderBy ) ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof OrderByExpr );
        assertArrayEquals( orderBy, ( (OrderByExpr) compareExpr.getRight() ).getFields() );

        assertEquals( CompareExpr.IN, compareExpr.getOperator() );
    }

    @Test
    public void evaluate_generates_empty_keyset_when_operation_is_IN_but_value_is_not_string()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        ValueExpr[] values = new ValueExpr[]{new ValueExpr( 1.0d ), new ValueExpr( 2.0d )};
        ArrayExpr arrayExpr = new ArrayExpr( values );

        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.IN, new FieldExpr( CONTENT_TYPE ), arrayExpr ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( "contenttypekey", ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof ArrayExpr );
        assertArrayEquals( new ValueExpr[0], ( (ArrayExpr) compareExpr.getRight() ).getValues() );

        assertEquals( CompareExpr.IN, compareExpr.getOperator() );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_IN_but_value_is_not_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( null ).times( 1 );
        replay( contentTypeDaoMock );

        ValueExpr[] values = new ValueExpr[]{new ValueExpr( "article" ), new ValueExpr( "document" )};
        ArrayExpr arrayExpr = new ArrayExpr( values );

        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.IN, new FieldExpr( CONTENT_TYPE ), arrayExpr ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof ArrayExpr );
        assertArrayEquals( values, ( (ArrayExpr) compareExpr.getRight() ).getValues() );

        assertEquals( CompareExpr.IN, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void evaluate_changes_value_when_operation_is_IN_and_value_is_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        ContentTypeEntity article = new ContentTypeEntity();
        article.setKey( 88 );

        ContentTypeEntity document = new ContentTypeEntity();
        document.setKey( 99 );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( article ).times( 1 );
        expect( contentTypeDaoMock.findByName( "document" ) ).andReturn( document ).times( 1 );
        replay( contentTypeDaoMock );

        ValueExpr[] values = new ValueExpr[]{new ValueExpr( "article" ), new ValueExpr( "document" )};
        ArrayExpr arrayExpr = new ArrayExpr( values );

        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.IN, new FieldExpr( CONTENT_TYPE ), arrayExpr ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( "contenttypekey", ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof ArrayExpr );
        assertArrayEquals( new ValueExpr[]{new ValueExpr( 88.0f ), new ValueExpr( 99.0f )},
                           ( (ArrayExpr) compareExpr.getRight() ).getValues() );

        assertEquals( CompareExpr.IN, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void evaluate_does_not_change_value_when_operation_is_IN_and_at_least_one_value_is_not_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        ContentTypeEntity article = new ContentTypeEntity();
        article.setKey( 88 );

        ContentTypeEntity document = new ContentTypeEntity();
        document.setKey( 99 );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( article ).times( 1 );
        expect( contentTypeDaoMock.findByName( "document" ) ).andReturn( document ).times( 1 );
        expect( contentTypeDaoMock.findByName( "wrong" ) ).andReturn( null ).times( 1 );
        replay( contentTypeDaoMock );

        ValueExpr[] values = new ValueExpr[]{new ValueExpr( "article" ), new ValueExpr( "document" ), new ValueExpr( "wrong" )};
        ArrayExpr arrayExpr = new ArrayExpr( values );

        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.IN, new FieldExpr( CONTENT_TYPE ), arrayExpr ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( CONTENT_TYPE, ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof ArrayExpr );
        assertArrayEquals( values, ( (ArrayExpr) compareExpr.getRight() ).getValues() );

        assertEquals( CompareExpr.IN, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    @Test
    public void evaluate_changes_value_when_operation_is_NOT_IN_and_value_is_preset_in_db()
    {
        ContentTypeEvaluator evaluator = new ContentTypeEvaluator( contentTypeDaoMock );

        ContentTypeEntity article = new ContentTypeEntity();
        article.setKey( 88 );

        ContentTypeEntity document = new ContentTypeEntity();
        document.setKey( 99 );

        expect( contentTypeDaoMock.findByName( "article" ) ).andReturn( article ).times( 1 );
        expect( contentTypeDaoMock.findByName( "document" ) ).andReturn( document ).times( 1 );
        replay( contentTypeDaoMock );

        ValueExpr[] values = new ValueExpr[]{new ValueExpr( "article" ), new ValueExpr( "document" )};
        ArrayExpr arrayExpr = new ArrayExpr( values );

        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.NOT_IN, new FieldExpr( CONTENT_TYPE ), arrayExpr ) );

        assertTrue( compareExpr.getLeft() instanceof FieldExpr );
        assertEquals( "contenttypekey", ( (FieldExpr) compareExpr.getLeft() ).getPath() );

        assertTrue( compareExpr.getRight() instanceof ArrayExpr );
        assertArrayEquals( new ValueExpr[]{new ValueExpr( 88.0f ), new ValueExpr( 99.0f )},
                           ( (ArrayExpr) compareExpr.getRight() ).getValues() );

        assertEquals( CompareExpr.NOT_IN, compareExpr.getOperator() );

        verify( contentTypeDaoMock );
    }

    private static void assertArrayEquals( Object[] a1, Object[] a2 )
    {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    private static String arrayToString( Object[] a )
    {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ )
        {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }
}
