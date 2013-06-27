/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query.factory;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderByExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderFieldExpr;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public class OrderQueryBuilderFactory
    extends BaseQueryBuilderFactory
{

    private final static boolean createDefaultSortExpression = false;


    public void buildOrderByExpr( final SearchSourceBuilder builder, final OrderByExpr expr )
    {
        List<SortBuilder> sorts;

        if ( expr != null )
        {
            sorts = buildOrderFieldExpr( expr.getFields() );

            for ( SortBuilder sort : sorts )
            {
                builder.sort( sort );
            }
        }
        else if ( createDefaultSortExpression )
        {
            sorts = new ArrayList<SortBuilder>();
            sorts.add( getDefaultSorting() );
        }

    }

    public void buildOrderBySection( final SearchSourceBuilder builder, final MenuItemKey section )
    {
        final String orderBySectionName = CONTENT_SECTION_ORDER_PREFIX + section.toString();
        final OrderFieldExpr orderFieldExpr = new OrderFieldExpr( new FieldExpr( orderBySectionName ), false );
        builder.sort( buildOrderFieldExpr( orderFieldExpr ) );
    }

    private ScoreSortBuilder getDefaultSorting()
    {
        return SortBuilders.scoreSort();
    }

    private List<SortBuilder> buildOrderFieldExpr( final OrderFieldExpr[] expr )
    {
        List<SortBuilder> sort = new ArrayList<SortBuilder>();

        for ( final OrderFieldExpr anExpr : expr )
        {
            sort.add( buildOrderFieldExpr( anExpr ) );
        }

        return sort;
    }

    private SortBuilder buildOrderFieldExpr( final OrderFieldExpr expr )
    {
        final String name = QueryFieldNameResolver.resolveOrderFieldName( expr.getField() );

        SortOrder order = SortOrder.DESC;

        if ( expr.isAscending() )
        {
            order = SortOrder.ASC;
        }

        return new FieldSortBuilder( name ).order( order ).ignoreUnmapped( true );
    }

}
