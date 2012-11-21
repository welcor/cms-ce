package com.enonic.cms.core.search.facet.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class RangeFacetModel
    extends AbstractFacetModel
{
    private String index;

    private String keyField;

    private String valueField;

    @XmlElement(name = "range")
    private Set<FacetRange> ranges = new LinkedHashSet<FacetRange>();

    public void addFacetRange( FacetRange facetRange )
    {
        this.ranges.add( facetRange );
    }

    @XmlElement(name = "index")
    public String getIndex()
    {
        return index;
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

    public void setIndex( final String index )
    {
        this.index = index;
    }

    public Set<FacetRange> getRanges()
    {
        return ranges;
    }

    public boolean isNumericRanges()
    {
        final FacetRange firstRange = this.ranges.iterator().next();

        return firstRange.isNumericRange();
    }

    @Override
    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( this.index ) && Strings.isNullOrEmpty( this.keyField ) )
        {
            throw new IllegalArgumentException( "Error in range-facet " + getName() + ": 'index' or 'keyField' must be set" );
        }

        if ( Strings.isNullOrEmpty( this.index ) && !Strings.isNullOrEmpty( this.keyField ) && Strings.isNullOrEmpty( this.valueField ) )
        {
            throw new IllegalArgumentException( "Error in range-facet " + getName() + ": both 'key-field' and 'value-field' must be set" );
        }

        validateFacetRanges();
    }

    public void validateFacetRanges()
    {
        if ( ranges.size() == 0 )
        {
            throw new IllegalArgumentException( "No ranges defined" );
        }

        boolean shouldBeNumericRanges = ranges.iterator().next().isNumericRange();

        for ( final FacetRange facetRange : this.ranges )
        {
            facetRange.validate();

            if ( shouldBeNumericRanges != facetRange.isNumericRange() )
            {
                throw new IllegalArgumentException( "All range-values must be of same type" );
            }
        }
    }

}
