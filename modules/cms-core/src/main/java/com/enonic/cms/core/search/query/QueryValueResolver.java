package com.enonic.cms.core.search.query;

import org.joda.time.ReadableDateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.enonic.cms.core.content.index.queryexpression.ArrayExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FunctionExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/13/12
 * Time: 4:29 PM
 */
public class QueryValueResolver
{

    public static QueryValue[] resolveQueryValues( Expression expr )
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
            return resolveQueryValues( (FunctionExpr) expr );
        }
        else
        {
            return new QueryValue[0];
        }
    }


    private static QueryValue[] toQueryValues( ArrayExpr expr )
    {
        final ValueExpr[] list = expr.getValues();
        final QueryValue[] result = new QueryValue[list.length];

        for ( int i = 0; i < list.length; i++ )
        {
            result[i] = toQueryValue( list[i] );
        }

        return result;
    }


    public static QueryValue toQueryValue( ValueExpr expr )
    {
        if ( expr.isDate() )
        {
            return new QueryValue( formatDateForElasticSearch( (ReadableDateTime) expr.getValue() ) );
        }
        else
        {
            return new QueryValue( expr.getValue() );
        }
    }

    private static String formatDateForElasticSearch( final ReadableDateTime date )
    {
        return ISODateTimeFormat.dateTime().print( date );
    }
}
