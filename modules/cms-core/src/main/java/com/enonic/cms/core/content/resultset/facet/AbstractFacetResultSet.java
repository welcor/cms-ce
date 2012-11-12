package com.enonic.cms.core.content.resultset.facet;

public abstract class AbstractFacetResultSet
{

    private Integer totalCount;

    private Integer missing;

    private Integer hits;

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount( final Integer totalCount )
    {
        this.totalCount = totalCount;
    }

    public Integer getMissing()
    {
        return missing;
    }

    public void setMissing( final Integer missing )
    {
        this.missing = missing;
    }

    public Integer getHits()
    {
        return hits;
    }

    public void setHits( final Integer hits )
    {
        this.hits = hits;
    }
}
