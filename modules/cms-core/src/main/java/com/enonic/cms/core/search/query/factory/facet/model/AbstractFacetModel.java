package com.enonic.cms.core.search.query.factory.facet.model;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.util.Assert;

public abstract class AbstractFacetModel
    implements FacetModel
{
    private String name;

    private Integer size;

    public void setName( final String name )
    {
        this.name = name;
    }

    @XmlElement(name = "name", required = true)
    public String getName()
    {
        return name;
    }

    @XmlElement(name = "size")
    public Integer getSize()
    {
        return size;
    }

    public void setSize( final Integer size )
    {
        this.size = size;
    }

    public void verify()
    {
        Assert.notNull( this.name, "Facet 'name' is required" );
    }

}

