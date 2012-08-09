package com.enonic.cms.core.content;


import java.util.Comparator;
import java.util.List;

import com.enonic.cms.core.content.category.CategoryKey;

public class OrderCategoryKeysByGivenOrderComparator
    implements Comparator<CategoryKey>
{
    private final List<CategoryKey> orderMask;

    public OrderCategoryKeysByGivenOrderComparator( final List<CategoryKey> orderMask )
    {
        this.orderMask = orderMask;
    }

    public int compare( final CategoryKey a, final CategoryKey b )
    {
        Integer order1 = orderMask.indexOf( a );
        Integer order2 = orderMask.indexOf( b );

        order1 = order1 == -1 ? Integer.MAX_VALUE : order1;
        order2 = order2 == -1 ? Integer.MAX_VALUE : order2;

        if ( order1.equals( order2 ) )
        {
            return 0;
        }

        return order1 > order2 ? 1 : -1;
    }
}