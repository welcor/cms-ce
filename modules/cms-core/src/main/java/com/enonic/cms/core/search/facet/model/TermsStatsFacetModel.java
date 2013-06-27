/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class TermsStatsFacetModel
    extends AbstractFacetModel
{
    private String index;

    private String valueIndex;

    private FacetOrderBy orderby;

    @XmlElement(name = "orderby")
    public String getOrderby()
    {
        return this.orderby != null ? this.orderby.getFacetOrderbyString().toLowerCase() : null;
    }

    public FacetOrderBy getFacetOrderBy()
    {
        return this.orderby;
    }

    public void setOrderby( final String orderby )
    {
        this.orderby = FacetOrderBy.createFacetOrderBy( orderby );
    }


    @XmlElement(name = "index")
    public String getIndex()
    {
        return index;
    }

    @XmlElement(name = "value-index")
    public String getValueIndex()
    {
        return valueIndex;
    }


    public void setIndex( final String index )
    {
        this.index = index;
    }

    public void setValueIndex( final String valueIndex )
    {
        this.valueIndex = valueIndex;
    }

    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( this.index ) || Strings.isNullOrEmpty( this.valueIndex ) )
        {
            throw new IllegalArgumentException( "Terms-stats-facet " + getName() + ": Fields 'index' and 'value-index' must be set" );
        }
    }

}
