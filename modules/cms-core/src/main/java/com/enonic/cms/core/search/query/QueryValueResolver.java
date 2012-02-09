package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.enonic.cms.core.content.index.queryexpression.ArrayExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FunctionEvaluator;
import com.enonic.cms.core.content.index.queryexpression.FunctionExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;
import com.enonic.cms.core.content.index.util.ValueConverter;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:18 AM
 */
public class QueryValueResolver
{

    public static Object[] toValues( Expression expr )
    {
        if ( expr instanceof ArrayExpr )
        {
            return toValues( (ArrayExpr) expr );
        }
        else if ( expr instanceof ValueExpr )
        {
            return new Object[]{toValue( (ValueExpr) expr )};
        }
        else if ( expr instanceof FunctionExpr )
        {
            return toValues( (FunctionExpr) expr );
        }
        else
        {
            return new String[0];
        }
    }

    public static Object toValue( ValueExpr expr )
    {
        if ( expr.isNumber() )
        {
            return expr.getValue();

        }
        else if ( expr.isDate() )
        {
            return formatDateForElasticSearch( (ReadableDateTime) expr.getValue() );
        }
        else if ( expr.isValidDateString() )
        {
            return formatDateStringForElasticSearch( (String) expr.getValue() );
        }
        else
        {
            final String stringValue = expr.getValue().toString();
            return StringUtils.lowerCase( stringValue );
        }
    }

    private static String formatDateStringForElasticSearch( final String dateValue )
    {
        return formatDateForElasticSearch( ValueConverter.toDate( dateValue ) );
    }

    private static String formatDateForElasticSearch( final ReadableDateTime date )
    {
        return ISODateTimeFormat.dateTime().print( toUTCTimeZone(date) );
    }

    private static ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }


    private static Object[] toValues( ArrayExpr expr )
    {
        final ValueExpr[] list = expr.getValues();
        final Object[] result = new Object[list.length];

        for ( int i = 0; i < list.length; i++ )
        {
            result[i] = toValue( list[i] );
        }

        return result;
    }

    private static Object[] toValues( FunctionExpr expr )
    {
        final FunctionEvaluator eval = new FunctionEvaluator();
        return toValues( (Expression) expr.evaluate( eval ) );
    }

}
