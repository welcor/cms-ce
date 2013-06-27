/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.index.queryexpression.ContentTypeEvaluator;
import com.enonic.cms.core.content.index.queryexpression.DateCompareEvaluator;
import com.enonic.cms.core.content.index.queryexpression.FunctionEvaluator;
import com.enonic.cms.core.content.index.queryexpression.IntegerFieldEvaluator;
import com.enonic.cms.core.content.index.queryexpression.QueryEvaluator;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryParser;
import com.enonic.cms.store.dao.ContentTypeDao;


public class ContentIndexQueryExprParser
{
    private static QueryEvaluator functionEvaluator = new FunctionEvaluator();

    private static QueryEvaluator dateCompareEvaluator = new DateCompareEvaluator();

    private static QueryEvaluator numberFieldEvaluator = new IntegerFieldEvaluator();

    public static QueryExpr parse( ContentIndexQuery query, ContentTypeDao contentTypeDao )
    {
        return doParse( query, true, contentTypeDao );
    }

    public static QueryExpr parse( ContentIndexQuery query, boolean convertNumerics, ContentTypeDao contentTypeDao )
    {
        return doParse( query, convertNumerics, contentTypeDao );
    }

    private static QueryExpr doParse( final ContentIndexQuery query, final boolean convertNumerics, ContentTypeDao contentTypeDao )
    {
        QueryExpr expr = QueryParser.newInstance().parse( query.getQuery() );

        // invoke any functions...
        expr = (QueryExpr) expr.evaluate( functionEvaluator );

        // convert numbers given as strings to real numbers
        if ( convertNumerics )
        {
            expr = (QueryExpr) expr.evaluate( numberFieldEvaluator );
        }

        // do some tricks with dates in some special cases...
        expr = (QueryExpr) expr.evaluate( dateCompareEvaluator );

        // do trick with contenttype
        expr = (QueryExpr) expr.evaluate( new ContentTypeEvaluator( contentTypeDao ) );

        return expr;
    }

}
