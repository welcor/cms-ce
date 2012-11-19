package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class RangeFacetModel
    extends AbstractFacetModel
{
    private String field;

    private String keyField;

    private String valueField;

    @XmlElement(name = "ranges")
    private FacetRanges facetRanges;

    @XmlElement
    public String getField()
    {
        return field;
    }

    @XmlElement(name = "key_field")
    public String getKeyField()
    {
        return keyField;
    }

    @XmlElement(name = "value_field")
    public String getValueField()
    {
        return valueField;
    }


    public void setKeyField( final String keyField )
    {
        this.keyField = keyField;
    }


    public void setValueField( final String valueField )
    {
        this.valueField = valueField;
    }

    public FacetRanges getFacetRanges()
    {
        return facetRanges;
    }

    public void setFacetRanges( final FacetRanges facetRanges )
    {
        this.facetRanges = facetRanges;
    }

    public void setField( final String field )
    {
        this.field = field;
    }

    @Override
    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( this.field ) && Strings.isNullOrEmpty( this.keyField ) )
        {
            throw new IllegalArgumentException( "Error in range-facet + " + getName() + ": 'field' or 'keyField' must be set" );
        }

        if ( Strings.isNullOrEmpty( this.field ) && !Strings.isNullOrEmpty( this.keyField ) && Strings.isNullOrEmpty( this.valueField ) )
        {
            throw new IllegalArgumentException(
                "Error in range-facet + " + getName() + ": both 'key-field' and 'value-field' must be set" );
        }

        facetRanges.validate();
    }

}
