/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.index.queryexpression.DateCompareEvaluator;
import com.enonic.cms.core.content.index.queryexpression.FunctionEvaluator;
import com.enonic.cms.core.content.index.queryexpression.IntegerFieldEvaluator;
import com.enonic.cms.core.content.index.queryexpression.QueryEvaluator;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryParser;


public class ContentIndexQueryExprParser
{
    private static QueryEvaluator functionEvaluator = new FunctionEvaluator();

    private static QueryEvaluator dateCompareEvaluator = new DateCompareEvaluator();

    private static QueryEvaluator numberFieldEvaluator = new IntegerFieldEvaluator();

    public static QueryExpr parse( ContentIndexQuery query )
    {
        QueryExpr expr = QueryParser.newInstance().parse( query.getQuery() );

        // invoke any functions...
        expr = (QueryExpr) expr.evaluate( functionEvaluator );

        // TODO: This should probably not be done anymore, since we should separate strings and numbers
        // convert numbers given as strings to real numbers
        // expr = (QueryExpr) expr.evaluate( numberFieldEvaluator );

        // do some tricks with dates in some special cases...
        expr = (QueryExpr) expr.evaluate( dateCompareEvaluator );

        return expr;
    }
}
