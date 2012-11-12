package com.enonic.cms.core.search.query.factory.facet.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
public class RangeFacetModel
    extends AbstractFacetModel
{
    private String field;

    private String keyField;

    private String valueField;

    private List<RangeXml> ranges;

    public void setField( final String field )
    {
        this.field = field;
    }

    public void addRange( final Integer low, final Integer high )
    {
        if ( this.ranges == null )
        {
            this.ranges = Lists.newArrayList();
        }

        this.ranges.add( new RangeXml( low, high ) );
    }

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

    public void setKeyField( final String keyField )
    {
        this.keyField = keyField;
    }

    @XmlElement(name = "value_field")
    public String getValueField()
    {
        return valueField;
    }

    public void setValueField( final String valueField )
    {
        this.valueField = valueField;
    }

    @XmlElementWrapper
    public List<RangeXml> getRanges()
    {
        return ranges;
    }

    public void setRanges( final List<RangeXml> ranges )
    {
        this.ranges = ranges;
    }

    private static class RangeXml
    {
        @XmlAttribute
        private Integer from;

        @XmlAttribute
        private Integer to;

        private RangeXml()
        {
        }

        private RangeXml( final Integer from, final Integer to )
        {
            this.from = from;
            this.to = to;
        }
    }
}
