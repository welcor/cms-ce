package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQueryExprParser;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.queryexpression.NotExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;


public class QueryTranslator
{
    public SearchSourceBuilder build( ContentIndexQuery contentIndexQuery )
        throws Exception
    {

        final QueryExpr queryExpr = applyFunctionsAndDateTranslations( contentIndexQuery );

        final SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.from( contentIndexQuery.getIndex() );

        builder.size( contentIndexQuery.getCount() );

        if ( contentIndexQuery.getContentOnlineAtFilter() != null )
        {
            applyContentPublishedAtFilter( builder, contentIndexQuery.getContentOnlineAtFilter() );
        }
        // final QueryExpr queryExpr = QueryParser.newInstance().parse( contentIndexQuery.getQuery() );

        final Expression expression = queryExpr.getExpr();
        final Expression optimizedExpr = expression; //new LogicalOrOptimizer().optimize( expression );

        final QueryBuilder queryBuilder = buildExpr( optimizedExpr );

        builder.query( queryBuilder );

        OrderQueryBuilder.buildOrderByExpr( builder, queryExpr.getOrderBy() );
        FilterQueryBuilder.buildFilterQuery( builder, contentIndexQuery );

        System.out.println( "****************************\n\r" + builder.toString() );

        return builder;
    }

    private QueryExpr applyFunctionsAndDateTranslations( ContentIndexQuery contentIndexQuery )
    {
        return ContentIndexQueryExprParser.parse( contentIndexQuery );
    }

    private void applyContentPublishedAtFilter( final SearchSourceBuilder builder, final DateTime dateTime )
    {
        final ReadableDateTime dateTimeRoundedDownToNearestMinute = toUTCTimeZone( dateTime.minuteOfHour().roundFloorCopy() );
        final RangeFilterBuilder publishFromFilter = FilterBuilders.rangeFilter( "publishfrom" ).lte( dateTimeRoundedDownToNearestMinute );

        final MissingFilterBuilder publishToMissing = FilterBuilders.missingFilter( "publishto" );
        final RangeFilterBuilder publishToGTDate = FilterBuilders.rangeFilter( "publishto" ).gt( dateTimeRoundedDownToNearestMinute );
        final OrFilterBuilder publishToFilter = FilterBuilders.orFilter( publishToMissing, publishToGTDate );

        final AndFilterBuilder filter = FilterBuilders.andFilter( publishFromFilter, publishToFilter );
        builder.filter( filter );
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
        final String path = QueryFieldNameResolver.resolveQueryFieldName( (FieldExpr) expr.getLeft() );
        final QueryPath queryPath = QueryPathResolver.resolveQueryPath( path );
        final QueryValue[] queryValues = QueryValueResolver.resolveQueryValues( expr.getRight() );
        final QueryValue querySingleValue = queryValues.length > 0 ? queryValues[0] : null;

        final int operator = expr.getOperator();

        switch ( operator )
        {
            case CompareExpr.EQ:
                return TermQueryBuilderCreator.buildTermQuery( queryPath, querySingleValue );
            case CompareExpr.NEQ:
                return buildNotQuery( TermQueryBuilderCreator.buildTermQuery( queryPath, querySingleValue ) );
            case CompareExpr.GT:
                return RangeQueryBuilder.buildRangeQuery( path, querySingleValue, null, false, true );
            case CompareExpr.GTE:
                return RangeQueryBuilder.buildRangeQuery( path, querySingleValue, null, true, true );
            case CompareExpr.LT:
                return RangeQueryBuilder.buildRangeQuery( path, null, querySingleValue, true, false );
            case CompareExpr.LTE:
                return RangeQueryBuilder.buildRangeQuery( path, null, querySingleValue, true, true );
            case CompareExpr.LIKE:
                return LikeQueryBuilderCreator.buildLikeQuery( queryPath, querySingleValue );
            case CompareExpr.NOT_LIKE:
                return buildNotQuery( LikeQueryBuilderCreator.buildLikeQuery( queryPath, querySingleValue ) );
            case CompareExpr.IN:
                return buildInQuery( path, queryValues );
            case CompareExpr.NOT_IN:
                return buildNotQuery( buildInQuery( path, queryValues ) );
            case CompareExpr.FT:
                return buildFulltextQuery( path, querySingleValue );
        }

        return null;
    }

    private QueryBuilder buildFulltextQuery( final String path, final QueryValue queryValue )
    {
        return QueryBuilders.termQuery( path + IndexFieldNameConstants.NON_ANALYZED_FIELD_POSTFIX, queryValue.getStringValueNormalized() );
    }

    private QueryBuilder buildNotExpr( NotExpr expr )
        throws Exception
    {
        final QueryBuilder negated = buildExpr( expr.getExpr() );
        return buildNotQuery( negated );
    }

    private QueryBuilder buildInQuery( String field, QueryValue[] values )
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for ( QueryValue value : values )
        {
            if ( value.isNumeric() )
            {
                boolQuery.should( QueryBuilders.termQuery( field, value.getDoubleValue() ) );
            }
            else
            {
                boolQuery.should( QueryBuilders.termQuery( field, value.getStringValueNormalized() ) );
            }
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

    private ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }

}
