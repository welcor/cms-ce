/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

public class HistogramFacetResultEntry
{
    private Long count;

    private Long key;

    private Double max;

    private Double min;

    private Double mean;

    private Double total;

    private Long totalCount;

    public Long getCount()
    {
        return count;
    }

    public void setCount( final Long count )
    {
        this.count = count;
    }

    public Long getKey()
    {
        return key;
    }

    public void setKey( final Long key )
    {
        this.key = key;
    }

    public Double getMax()
    {
        return max;
    }

    public void setMax( final Double max )
    {
        this.max = max;
    }

    public Double getMin()
    {
        return min;
    }

    public void setMin( final Double min )
    {
        this.min = min;
    }

    public Double getMean()
    {
        return mean;
    }

    public void setMean( final Double mean )
    {
        this.mean = mean;
    }

    public Double getTotal()
    {
        return total;
    }

    public void setTotal( final Double total )
    {
        this.total = total;
    }

    public Long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount( final Long totalCount )
    {
        this.totalCount = totalCount;
    }
}
