/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query;

import java.util.Collection;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQueryExprParser;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.LogicalExpr;
import com.enonic.cms.core.content.index.queryexpression.NotExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderByExpr;
import com.enonic.cms.core.content.index.queryexpression.QueryExpr;
import com.enonic.cms.core.search.IndexException;
import com.enonic.cms.core.search.facet.FacetBuilderFactory;
import com.enonic.cms.core.search.query.factory.FilterQueryBuilderFactory;
import com.enonic.cms.core.search.query.factory.FullTextQueryBuilderFactory;
import com.enonic.cms.core.search.query.factory.InQueryBuilderFactory;
import com.enonic.cms.core.search.query.factory.LikeQueryBuilderFactory;
import com.enonic.cms.core.search.query.factory.OrderQueryBuilderFactory;
import com.enonic.cms.core.search.query.factory.RangeQueryBuilderFactory;
import com.enonic.cms.core.search.query.factory.TermQueryBuilderFactory;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.store.dao.ContentTypeDao;

@Component
public class QueryTranslator
{
    private final FilterQueryBuilderFactory filterQueryBuilderFactory;

    private final OrderQueryBuilderFactory orderQueryBuilderFactory;

    private final TermQueryBuilderFactory termQueryBuilderFactory;

    private final RangeQueryBuilderFactory rangeQueryBuilderFactory;

    private final LikeQueryBuilderFactory likeQueryBuilderFactory;

    private final InQueryBuilderFactory inQueryBuilderFactory;

    private final FullTextQueryBuilderFactory fullTextQueryBuilderFactory;

    private final FacetBuilderFactory facetBuilderFactory;

    @Autowired
    private ContentTypeDao contentTypeDao;

    public QueryTranslator()
    {
        filterQueryBuilderFactory = new FilterQueryBuilderFactory();
        orderQueryBuilderFactory = new OrderQueryBuilderFactory();
        termQueryBuilderFactory = new TermQueryBuilderFactory();
        rangeQueryBuilderFactory = new RangeQueryBuilderFactory();
        likeQueryBuilderFactory = new LikeQueryBuilderFactory();
        inQueryBuilderFactory = new InQueryBuilderFactory();
        fullTextQueryBuilderFactory = new FullTextQueryBuilderFactory();
        facetBuilderFactory = new FacetBuilderFactory();
    }


    public SearchSourceBuilder build( final ContentIndexQuery contentIndexQuery )
    {
        return doBuildSearchSource( contentIndexQuery, contentIndexQuery.getCount() );
    }

    public SearchSourceBuilder build( final ContentIndexQuery contentIndexQuery, int count )
    {
        return doBuildSearchSource( contentIndexQuery, count );
    }

    private SearchSourceBuilder doBuildSearchSource( final ContentIndexQuery contentIndexQuery, int count )
    {
        final QueryExpr queryExpr = applyFunctionsAndDateTranslations( contentIndexQuery );

        final SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from( contentIndexQuery.getIndex() );
        builder.size( count );

        final Expression expression = queryExpr.getExpr();

        final QueryBuilder builtQuery;
        try
        {
            builtQuery = buildQuery( expression );
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build query: " + contentIndexQuery.toString(), e );
        }

        applySorting( builder, contentIndexQuery, queryExpr.getOrderBy() );

        doAddFilters( contentIndexQuery, builder, builtQuery );

        doAddFacets( contentIndexQuery, builder );

        //System.out.println( builder.toString() );

        return builder;
    }

    private void doAddFilters( final ContentIndexQuery contentIndexQuery, final SearchSourceBuilder builder, final QueryBuilder builtQuery )
    {
        final FilterBuilder filtersToApply = filterQueryBuilderFactory.buildFilter( contentIndexQuery );
        FilteredQueryBuilder filterQueryBuilder = new FilteredQueryBuilder( builtQuery, filtersToApply );
        builder.query( filterQueryBuilder );
    }

    private void doAddFacets( final ContentIndexQuery contentIndexQuery, final SearchSourceBuilder builder )
    {
        final Collection<AbstractFacetBuilder> facetBuilders = facetBuilderFactory.buildFacetBuilder( contentIndexQuery );
        for ( AbstractFacetBuilder facetBuilder : facetBuilders )
        {
            builder.facet( facetBuilder );
        }
    }

    private void applySorting( SearchSourceBuilder builder, ContentIndexQuery contentIndexQuery, OrderByExpr orderByExpr )
    {
        final MenuItemKey orderBySection = contentIndexQuery.getOrderBySection();
        if ( orderBySection != null )
        {
            orderQueryBuilderFactory.buildOrderBySection( builder, orderBySection );
        }
        else
        {
            orderQueryBuilderFactory.buildOrderByExpr( builder, orderByExpr );
        }
    }

    private QueryExpr applyFunctionsAndDateTranslations( final ContentIndexQuery contentIndexQuery )
    {
        return ContentIndexQueryExprParser.parse( contentIndexQuery, false, contentTypeDao );
    }

    private QueryBuilder buildQuery( final Expression expr )
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


    private QueryBuilder buildCompareExpr( final CompareExpr expr )
    {
        final String path = QueryFieldNameResolver.resolveQueryFieldName( (FieldExpr) expr.getLeft() );
        final QueryField queryField = QueryFieldFactory.resolveQueryField( path );
        final QueryValue[] queryValues = QueryValueFactory.resolveQueryValues( expr.getRight() );
        final QueryValue querySingleValue = queryValues.length > 0 ? queryValues[0] : null;

        final QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( queryField, querySingleValue );

        final int operator = expr.getOperator();

        switch ( operator )
        {
            case CompareExpr.EQ:
                return termQueryBuilderFactory.buildTermQuery( queryFieldAndValue );
            case CompareExpr.NEQ:
                return buildNotQuery( termQueryBuilderFactory.buildTermQuery( queryFieldAndValue ) );
            case CompareExpr.GT:
                return rangeQueryBuilderFactory.buildRangeQuery( queryField, querySingleValue, null, false, true );
            case CompareExpr.GTE:
                return rangeQueryBuilderFactory.buildRangeQuery( queryField, querySingleValue, null, true, true );
            case CompareExpr.LT:
                return rangeQueryBuilderFactory.buildRangeQuery( queryField, null, querySingleValue, true, false );
            case CompareExpr.LTE:
                return rangeQueryBuilderFactory.buildRangeQuery( queryField, null, querySingleValue, true, true );
            case CompareExpr.LIKE:
                return likeQueryBuilderFactory.buildLikeQuery( queryFieldAndValue );
            case CompareExpr.NOT_LIKE:
                return buildNotQuery( likeQueryBuilderFactory.buildLikeQuery( queryFieldAndValue ) );
            case CompareExpr.IN:
                return inQueryBuilderFactory.buildInQuery( queryField, queryValues );
            case CompareExpr.NOT_IN:
                return buildNotQuery( inQueryBuilderFactory.buildInQuery( queryField, queryValues ) );
            case CompareExpr.FT:
                return fullTextQueryBuilderFactory.buildFulltextQuery( path, querySingleValue );
        }

        return null;
    }

    private QueryBuilder buildNotExpr( final NotExpr expr )
        throws Exception
    {
        final QueryBuilder negated = buildQuery( expr.getExpr() );
        return buildNotQuery( negated );
    }

    private QueryBuilder buildLogicalExpr( final LogicalExpr expr )
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

    private QueryBuilder buildNotQuery( final QueryBuilder negated )
    {
        return QueryBuilders.boolQuery().must( QueryBuilders.matchAllQuery() ).mustNot( negated );
    }

}
