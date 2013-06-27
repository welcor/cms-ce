/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

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

    private String valueIndex;

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

    @XmlElement(name = "value-index")
    public String getValueIndex()
    {
        return valueIndex;
    }

    public void setValueIndex( final String valueIndex )
    {
        this.valueIndex = valueIndex;
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

        if ( Strings.isNullOrEmpty( this.index ) )
        {
            throw new IllegalArgumentException( "Error in range-facet " + getName() + ": 'index' must be set" );
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
