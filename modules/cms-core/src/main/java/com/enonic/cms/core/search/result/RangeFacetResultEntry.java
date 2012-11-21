package com.enonic.cms.core.search.result;

public class RangeFacetResultEntry
{
    private String from;

    private String to;

    private Long count;

    private Double min;

    private Double max;

    private Double mean;

    public String getFrom()
    {
        return from;
    }

    public void setFrom( final String from )
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo( final String to )
    {
        this.to = to;
    }

    public Long getCount()
    {
        return count;
    }

    public void setCount( final Long count )
    {
        this.count = count;
    }

    public Double getMin()
    {
        return min;
    }

    public void setMin( final Double min )
    {
        this.min = min;
    }

    public Double getMax()
    {
        return max;
    }

    public void setMax( final Double max )
    {
        this.max = max;
    }

    public Double getMean()
    {
        return mean;
    }

    public void setMean( final Double mean )
    {
        this.mean = mean;
    }
}
