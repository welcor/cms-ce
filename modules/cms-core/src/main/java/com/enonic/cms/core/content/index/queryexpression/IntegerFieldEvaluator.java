package com.enonic.cms.core.content.index.queryexpression;

/**
 * Validates integer fields and convert integers represented as strings into actual integers.
 */
public final class IntegerFieldEvaluator
    extends QueryEvaluatorAdapter
{
    public Object evaluate( CompareExpr expr )
    {
        Expression left = (Expression) expr.getLeft().evaluate( this );
        Expression right = (Expression) expr.getRight().evaluate( this );

        if ( isIntegerField( left ) && right instanceof ValueExpr )
        {
            ValueExpr valueExpr = (ValueExpr) right;

            if ( valueExpr.isString() )
            {
                String s = (String) valueExpr.getValue();
                validateInteger( valueExpr.getValue(), expr );
                return new CompareExpr( expr.getOperator(), left, new ValueExpr( new Double( s ) ) );
            }
            else if ( !valueExpr.isNumber() )
            {
                throw new QueryParserException( "Expected integer" +
                                                    " on right side of expression: " + expr );
            }
            else if ( valueExpr.isNumber() )
            {
                validateInteger( valueExpr.getValue(), expr );
            }
        }
        return expr;
    }

    private boolean isIntegerField( Expression expr )
    {
        return ( expr instanceof FieldExpr ) && ( (FieldExpr) expr ).isIntegerField();
    }

    private void validateInteger( Object value, Expression expr )
    {
        if ( value instanceof Double )
        {
            Double d = (Double) value;
            if ( d.intValue() != d )
            {
                throw new QueryParserException( "Expected integer on right side of expression: " + expr );
            }
            else
            {
                return;
            }
        }

        try
        {
            new Integer( String.valueOf( value ) );
        }
        catch ( NumberFormatException e )
        {
            throw new QueryParserException( "Expected integer on right side of expression: " + expr );
        }
    }

}
