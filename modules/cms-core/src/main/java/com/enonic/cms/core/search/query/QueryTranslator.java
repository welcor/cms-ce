/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQueryExprParser;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.queryexpression.NotExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;

@Component
public class QueryTranslator
{
    private final static Logger LOG = Logger.getLogger( QueryTranslator.class.getName() );

    private final FilterQueryBuilderFactory filterQueryBuilderFactory;

    private final OrderQueryBuilderFactory orderQueryBuilderFactory;

    private final TermQueryBuilderFactory termQueryBuilderFactory;

    private final RangeQueryBuilderFactory rangeQueryBuilderFactory;

    private final LikeQueryBuilderFactory likeQueryBuilderFactory;

    private final InQueryBuilderFactory inQueryBuilderFactory;

    private final FullTextQueryBuilderFactory fullTextQueryBuilderFactory;


    public QueryTranslator()
    {
        filterQueryBuilderFactory = new FilterQueryBuilderFactory();
        orderQueryBuilderFactory = new OrderQueryBuilderFactory();
        termQueryBuilderFactory = new TermQueryBuilderFactory();
        rangeQueryBuilderFactory = new RangeQueryBuilderFactory();
        likeQueryBuilderFactory = new LikeQueryBuilderFactory();
        inQueryBuilderFactory = new InQueryBuilderFactory();
        fullTextQueryBuilderFactory = new FullTextQueryBuilderFactory();
    }


    public SearchSourceBuilder build( ContentIndexQuery contentIndexQuery )
        throws Exception
    {
        final QueryExpr queryExpr = applyFunctionsAndDateTranslations( contentIndexQuery );

        final SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from( contentIndexQuery.getIndex() );
        builder.size( contentIndexQuery.getCount() );

        final Expression expression = queryExpr.getExpr();

        final QueryBuilder builtQuery = buildQuery( expression );
        builder.query( builtQuery );

        orderQueryBuilderFactory.buildOrderByExpr( builder, queryExpr.getOrderBy() );
        filterQueryBuilderFactory.buildFilterQuery( builder, contentIndexQuery );

        if ( LOG.isLoggable( Level.INFO ) )
        {
            LOG.info( "ES query:\r\n" + builder.toString() );
        }

        return builder;
    }

    private QueryExpr applyFunctionsAndDateTranslations( ContentIndexQuery contentIndexQuery )
    {
        return ContentIndexQueryExprParser.parse( contentIndexQuery );
    }

    private QueryBuilder buildQuery( Expression expr )
        throws Exception
    {
        if ( expr == null )
        {
            return QueryBuilders.matchAllQuery();
        }

        if ( expr instanceof CompareExpr )
        {
            return buildCompareExpr( (CompareExpr) expr );
        }

        if ( expr instanceof LogicalExpr )
        {
            return buildLogicalExpr( (LogicalExpr) expr );
        }

        if ( expr instanceof NotExpr )
        {
            return buildNotExpr( (NotExpr) expr );
        }

        throw new QueryTranslatorException( expr.getClass().getName() + " expression not supported" );
    }


    private QueryBuilder buildCompareExpr( CompareExpr expr )
    {
        final String path = QueryFieldNameResolver.resolveQueryFieldName( (FieldExpr) expr.getLeft() );
        final QueryPath queryPath = QueryPathResolver.resolveQueryPath( path );
        final QueryValue[] queryValues = QueryValueResolver.resolveQueryValues( expr.getRight() );
        final QueryValue querySingleValue = queryValues.length > 0 ? queryValues[0] : null;

        final int operator = expr.getOperator();

        switch ( operator )
        {
            case CompareExpr.EQ:
                return termQueryBuilderFactory.buildTermQuery( queryPath, querySingleValue );
            case CompareExpr.NEQ:
                return buildNotQuery( termQueryBuilderFactory.buildTermQuery( queryPath, querySingleValue ) );
            case CompareExpr.GT:
                return rangeQueryBuilderFactory.buildRangeQuery( queryPath, querySingleValue, null, false, true );
            case CompareExpr.GTE:
                return rangeQueryBuilderFactory.buildRangeQuery( queryPath, querySingleValue, null, true, true );
            case CompareExpr.LT:
                return rangeQueryBuilderFactory.buildRangeQuery( queryPath, null, querySingleValue, true, false );
            case CompareExpr.LTE:
                return rangeQueryBuilderFactory.buildRangeQuery( queryPath, null, querySingleValue, true, true );
            case CompareExpr.LIKE:
                return likeQueryBuilderFactory.buildLikeQuery( queryPath, querySingleValue );
            case CompareExpr.NOT_LIKE:
                return buildNotQuery( likeQueryBuilderFactory.buildLikeQuery( queryPath, querySingleValue ) );
            case CompareExpr.IN:
                return inQueryBuilderFactory.buildInQuery( path, queryValues );
            case CompareExpr.NOT_IN:
                return buildNotQuery( inQueryBuilderFactory.buildInQuery( path, queryValues ) );
            case CompareExpr.FT:
                return fullTextQueryBuilderFactory.buildFulltextQuery( path, querySingleValue );
        }

        return null;
    }

    private QueryBuilder buildNotExpr( NotExpr expr )
        throws Exception
    {
        final QueryBuilder negated = buildQuery( expr.getExpr() );
        return buildNotQuery( negated );
    }

    private QueryBuilder buildLogicalExpr( LogicalExpr expr )
        throws Exception
    {

        final QueryBuilder left = buildQuery( expr.getLeft() );
        final QueryBuilder right = buildQuery( expr.getRight() );

        if ( expr.getOperator() == LogicalExpr.OR )
        {
            return QueryBuilders.boolQuery().should( left ).should( right );
        }
        else if ( expr.getOperator() == LogicalExpr.AND )
        {
            return QueryBuilders.boolQuery().must( left ).must( right );
        }
        else
        {
            throw new IllegalArgumentException( "Operation [" + expr.getToken() + "] not supported" );
        }
    }

    // TODO: Why? Should not have to create a match all and then add the negated?
    private QueryBuilder buildNotQuery( QueryBuilder negated )
    {
        return QueryBuilders.boolQuery().must( QueryBuilders.matchAllQuery() ).mustNot( negated );
    }

}
