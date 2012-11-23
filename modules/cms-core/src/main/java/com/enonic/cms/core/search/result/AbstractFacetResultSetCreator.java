package com.enonic.cms.core.search.result;

public abstract class AbstractFacetResultSetCreator
{
    protected Double getValueIfNumber( final double entry )
    {
        return Double.isNaN( entry ) ? null : entry;
    }
}
