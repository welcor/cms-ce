package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class TermsStatsFacetModel
    extends AbstractFacetModel
{
    private String orderby;

    private String keyIndex;

    private String valueIndex;


    @XmlElement(name = "key-index")
    public String getKeyIndex()
    {
        return keyIndex;
    }

    @XmlElement(name = "value-index")
    public String getValueIndex()
    {
        return valueIndex;
    }

    @XmlElement(name = "orderby")
    public String getOrderby()
    {
        return orderby;
    }

    public void setOrderby( final String orderby )
    {
        this.orderby = orderby;
    }

    public void setKeyIndex( final String keyIndex )
    {
        this.keyIndex = keyIndex;
    }

    public void setValueIndex( final String valueIndex )
    {
        this.valueIndex = valueIndex;
    }

    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( this.keyIndex ) || Strings.isNullOrEmpty( this.valueIndex ) )
        {
            throw new IllegalArgumentException( "Terms-stats-facet " + getName() + ": Fields 'key-index' and 'value-index' must be set" );
        }
    }

}
