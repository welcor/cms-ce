/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;

public abstract class AbstractFacetModel
    implements FacetModel
{
    private String name;

    private Integer count;

    public void setName( final String name )
    {
        this.name = name;
    }

    @XmlAttribute(name = "name")
    public String getName()
    {
        return name;
    }

    @XmlElement(name = "count")
    public Integer getCount()
    {
        return count;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void validate()
    {
        if ( Strings.isNullOrEmpty( this.name ) )
        {
            throw new FacetQueryException( "Facet must specify name" );
        }
    }


}

