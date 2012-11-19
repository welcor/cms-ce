package com.enonic.cms.core.search.result;

public class RangeFacetResultEntry
{
    private String from;

    private String to;

    private Long count;

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

}
