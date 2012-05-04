package com.enonic.cms.core.content;


import java.util.Comparator;
import java.util.List;

public class OrderContentKeysByGivenOrderComparator
    implements Comparator<ContentKey>
{
    private final List<ContentKey> orderMask;

    public OrderContentKeysByGivenOrderComparator( final List<ContentKey> orderMask )
    {
        this.orderMask = orderMask;
    }

    public int compare( final ContentKey a, final ContentKey b )
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