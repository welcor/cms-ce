package com.enonic.cms.core.search.result;

public abstract class AbstractFacetResultSet
    implements FacetResultSet
{
    private Long total;

    private Long missing;

    private Long other;

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public Long getTotal()
    {
        return total;
    }

    public void setTotal( final Long total )
    {
        this.total = total;
    }

    public Long getMissing()
    {
        return missing;
    }

    public void setMissing( final Long missing )
    {
        this.missing = missing;
    }

    public Long getOther()
    {
        return other;
    }

    public void setOther( final Long other )
    {
        this.other = other;
    }
}
