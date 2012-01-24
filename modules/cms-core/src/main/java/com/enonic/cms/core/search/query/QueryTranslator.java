package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQueryExprParser;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.queryexpression.NotExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;


public final class QueryTranslator
{

    public SearchSourceBuilder build( ContentIndexQuery contentIndexQuery )
        throws Exception
    {

        final QueryExpr queryExpr = applyFunctionsAndDateTranslations( contentIndexQuery );

        final SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.from( contentIndexQuery.getIndex() );

        builder.size( contentIndexQuery.getCount() );

        // final QueryExpr queryExpr = QueryParser.newInstance().parse( contentIndexQuery.getQuery() );

        final Expression expression = queryExpr.getExpr();
        final Expression optimizedExpr = expression; //new LogicalOrOptimizer().optimize( expression );

        final QueryBuilder queryBuilder = buildExpr( optimizedExpr );

        builder.query( queryBuilder );

        OrderQueryBuilder.buildOrderByExpr( builder, queryExpr.getOrderBy() );
        FilterQueryBuilder.buildFilterQuery( builder, contentIndexQuery );

        //System.out.println( "****************************\n\r" + builder.toString() );

        return builder;
    }

    private QueryExpr applyFunctionsAndDateTranslations( ContentIndexQuery contentIndexQuery )
    {
        return ContentIndexQueryExprParser.parse( contentIndexQuery );
    }

    private QueryBuilder buildExpr( Expression expr )
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

        final int operator = expr.getOperator();
        final String path = QueryFieldNameResolver.resolveQueryFieldName( (FieldExpr) expr.getLeft() );

        final QueryPath queryPath = QueryPathCreator.createQueryPath( path );

        final Object[] values = QueryValueResolver.toValues( expr.getRight() );
        final Object singleValue = values.length > 0 ? values[0] : null;

        switch ( operator )
        {
            case CompareExpr.EQ:
                return TermQueryBuilderCreator.buildTermQuery( queryPath, singleValue );
            case CompareExpr.NEQ:
                return buildNotQuery( TermQueryBuilderCreator.buildTermQuery( queryPath, singleValue ) );
            case CompareExpr.GT:
                return RangeQueryBuilder.buildRangeQuery( path, singleValue, null, false, true );
            case CompareExpr.GTE:
                return RangeQueryBuilder.buildRangeQuery( path, singleValue, null, true, true );
            case CompareExpr.LT:
                return RangeQueryBuilder.buildRangeQuery( path, null, singleValue, true, false );
            case CompareExpr.LTE:
                return RangeQueryBuilder.buildRangeQuery( path, null, singleValue, true, true );
            case CompareExpr.LIKE:
                return LikeQueryBuilderCreator.buildLikeQuery( queryPath, (String) singleValue );
            case CompareExpr.NOT_LIKE:
                return buildNotQuery( LikeQueryBuilderCreator.buildLikeQuery( queryPath, (String) singleValue ) );
            case CompareExpr.IN:
                return buildInQuery( path, values );
            case CompareExpr.NOT_IN:
                return buildNotQuery( buildInQuery( path, values ) );
            case CompareExpr.FT:
                return buildFulltextQuery( path, singleValue );
        }

        return null;
    }

    private QueryBuilder buildFulltextQuery( final String path, final Object singleValue )
    {
        String stringValue = (String) singleValue;
        return QueryBuilders.termQuery( path + IndexFieldNameConstants.NON_ANALYZED_FIELD_POSTFIX, stringValue );
    }

    private QueryBuilder buildNotExpr( NotExpr expr )
        throws Exception
    {
        final QueryBuilder negated = buildExpr( expr.getExpr() );
        return buildNotQuery( negated );
    }

    private QueryBuilder buildInQuery( String field, Object[] values )
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( Object value : values )
        {
            boolQuery.should( QueryBuilders.termQuery( field, value ) );
        }

        return boolQuery;
    }

    private QueryBuilder buildLogicalExpr( LogicalExpr expr )
        throws Exception
    {

        final QueryBuilder left = buildExpr( expr.getLeft() );
        final QueryBuilder right = buildExpr( expr.getRight() );

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
