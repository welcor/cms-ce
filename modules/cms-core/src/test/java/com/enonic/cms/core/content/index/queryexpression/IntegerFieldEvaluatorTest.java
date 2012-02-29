package com.enonic.cms.core.content.index.queryexpression;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerFieldEvaluatorTest
{
    @Test
    public void evaluate_turns_integer_as_string_into_double_when_left_is_integer_field()
    {
        IntegerFieldEvaluator evaluator = new IntegerFieldEvaluator();
        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "status" ), new ValueExpr( "0" ) ) );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isNumber() );
        assertEquals( 0.0, ( (ValueExpr) compareExpr.getRight() ).getValue() );
    }

    @Test
    public void evaluate_does_not_change_value_when_left_is_not_an_integer_field()
    {
        IntegerFieldEvaluator evaluator = new IntegerFieldEvaluator();
        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "fullText" ), new ValueExpr( "abc" ) ) );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isString() );
        assertEquals( "abc", ( (ValueExpr) compareExpr.getRight() ).getValue() );
    }

    @Test
    public void evaluate_does_not_change_value_when_right_is_double_but_actual_integer()
    {
        IntegerFieldEvaluator evaluator = new IntegerFieldEvaluator();
        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "status" ), new ValueExpr( 2.0 ) ) );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isNumber() );
        assertEquals( 2.0, ( (ValueExpr) compareExpr.getRight() ).getValue() );
    }

    @Test
    public void evaluate_turns_number_value_as_string_into_a_double_value()
    {
        IntegerFieldEvaluator evaluator = new IntegerFieldEvaluator();
        CompareExpr compareExpr =
            (CompareExpr) evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "status" ), new ValueExpr( "0" ) ) );

        assertTrue( ( (ValueExpr) compareExpr.getRight() ).isNumber() );
        assertEquals( 0.0, ( (ValueExpr) compareExpr.getRight() ).getValue() );
    }

    @Test(expected = QueryParserException.class)
    public void evaluate_throws_exception_when_left_is_integer_field_and_right_is_letter()
    {
        IntegerFieldEvaluator evaluator = new IntegerFieldEvaluator();
        evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "status" ), new ValueExpr( "a" ) ) );
    }

    @Test(expected = QueryParserException.class)
    public void evaluate_throws_exception_when_left_is_integer_field_and_right_is_decimal()
    {
        IntegerFieldEvaluator evaluator = new IntegerFieldEvaluator();
        evaluator.evaluate( new CompareExpr( CompareExpr.EQ, new FieldExpr( "status" ), new ValueExpr( 1.1 ) ) );
    }
}
