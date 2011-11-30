package com.enonic.cms.core.search.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.cms.core.content.index.queryexpression.OrderByExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderFieldExpr;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/20/11
 * Time: 9:02 AM
 */
public class OrderQueryBuilder
        extends BaseQueryBuilder
{

    public static void buildOrderByExpr( SearchSourceBuilder builder, OrderByExpr expr )
    {
        List<SortBuilder> sorts;

        if ( expr != null )
        {
            sorts = buildOrderFieldExpr( expr.getFields() );
        }
        else
        {
            sorts = new ArrayList<SortBuilder>();
            sorts.add( getDefaultSorting() );
        }

        for ( SortBuilder sort : sorts )
        {
            builder.sort( sort );
        }
    }

    private static ScoreSortBuilder getDefaultSorting()
    {
        return SortBuilders.scoreSort();
    }

    private static List<SortBuilder> buildOrderFieldExpr( OrderFieldExpr[] expr )
    {

        List<SortBuilder> sort = new ArrayList<SortBuilder>();

        for ( int i = 0; i < expr.length; i++ )
        {
            sort.add( buildOrderFieldExpr( expr[i] ) );
        }

        return sort;
    }

    private static SortBuilder buildOrderFieldExpr( OrderFieldExpr expr )
    {
        final String name = QueryFieldNameResolver.getOrderByFieldName( expr.getField() );

        SortOrder order = SortOrder.DESC;

        if ( expr.isAscending() )
        {
            order = SortOrder.ASC;
        }

        return new FieldSortBuilder( name ).order( order );
    }

}
