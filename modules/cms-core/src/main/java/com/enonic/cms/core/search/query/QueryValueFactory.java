/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import com.enonic.cms.core.content.index.queryexpression.ArrayExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FunctionExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;

public class QueryValueFactory
{
    public static QueryValue[] resolveQueryValues( final Expression expr )
    {
        if ( expr instanceof ArrayExpr )
        {
            return toQueryValues( (ArrayExpr) expr );
        }
        else if ( expr instanceof ValueExpr )
        {
            return new QueryValue[]{toQueryValue( (ValueExpr) expr )};
        }
        else if ( expr instanceof FunctionExpr )
        {
            return resolveQueryValues( expr );
        }
        else
        {
            return new QueryValue[0];
        }
    }


    private static QueryValue[] toQueryValues( final ArrayExpr expr )
    {
        final ValueExpr[] list = expr.getValues();
        final QueryValue[] result = new QueryValue[list.length];

        for ( int i = 0; i < list.length; i++ )
        {
            result[i] = toQueryValue( list[i] );
        }

        return result;
    }


    private static QueryValue toQueryValue( ValueExpr expr )
    {
        return new QueryValue( expr.getValue() );
    }

}
