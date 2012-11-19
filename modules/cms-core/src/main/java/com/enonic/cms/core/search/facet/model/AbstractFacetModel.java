package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;

public abstract class AbstractFacetModel
    implements FacetModel
{
    private String name;

    private Integer size;

    public void setName( final String name )
    {
        this.name = name;
    }

    @XmlAttribute(name = "name")
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

    public void validate()
    {
        if ( Strings.isNullOrEmpty( this.name ) )
        {
            throw new FacetQueryException( "Facet must specify name" );
        }
    }
}

