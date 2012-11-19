package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class HistogramFacetModel
    extends AbstractFacetModel
{

    private String field;

    private Long interval;

    private String keyField;

    private String valueField;

    @XmlElement(name = "field")
    public String getField()
    {
        return field;
    }

    public void setField( final String field )
    {
        this.field = field;
    }

    @XmlElement(name = "interval")
    public long getInterval()
    {
        return interval;
    }

    public void setInterval( final long interval )
    {
        this.interval = interval;
    }

    @XmlElement(name = "key-field")
    public String getKeyField()
    {
        return keyField;
    }

    public void setKeyField( final String keyField )
    {
        this.keyField = keyField;
    }

    @XmlElement(name = "value-field")
    public String getValueField()
    {
        return valueField;
    }

    public void setValueField( final String valueField )
    {
        this.valueField = valueField;
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

        if ( this.interval == null )
        {
            throw new IllegalArgumentException( "Error in range-facet + " + getName() + ": 'interval' must be set" );
        }

    }


}

