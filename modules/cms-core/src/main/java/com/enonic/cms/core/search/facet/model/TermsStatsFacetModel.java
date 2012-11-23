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

    private String keyField;

    private String valueField;


    @XmlElement(name = "key-field")
    public String getKeyField()
    {
        return keyField;
    }

    @XmlElement(name = "value-field")
    public String getValueField()
    {
        return valueField;
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

    public void setKeyField( final String keyField )
    {
        this.keyField = keyField;
    }

    public void setValueField( final String valueField )
    {
        this.valueField = valueField;
    }

    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( this.keyField ) || Strings.isNullOrEmpty( this.valueField ) )
        {
            throw new IllegalArgumentException( "Terms-stats-facet " + getName() + ": Fields 'key-field' and 'value-field' must be set" );
        }
    }

}
