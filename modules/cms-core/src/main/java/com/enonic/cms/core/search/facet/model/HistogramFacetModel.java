package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class HistogramFacetModel
    extends AbstractFacetModel
{
    private String index;

    private Long interval;

    private String keyField;

    private String valueField;

    @XmlElement(name = "index")
    public String getIndex()
    {
        return index;
    }

    public void setIndex( final String index )
    {
        this.index = index;
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

        if ( Strings.isNullOrEmpty( this.index ) && Strings.isNullOrEmpty( this.keyField ) )
        {
            throw new IllegalArgumentException( "Error in histogram-facet  " + getName() + ": 'field' or 'keyField' must be set" );
        }

        if ( Strings.isNullOrEmpty( this.index ) && !Strings.isNullOrEmpty( this.keyField ) && Strings.isNullOrEmpty( this.valueField ) )
        {
            throw new IllegalArgumentException(
                "Error in histogram-facet  " + getName() + ": both 'key-field' and 'value-field' must be set" );
        }

        if ( this.interval == null )
        {
            throw new IllegalArgumentException( "Error in histogram-facet " + getName() + ": 'interval' must be set" );
        }

    }


}

