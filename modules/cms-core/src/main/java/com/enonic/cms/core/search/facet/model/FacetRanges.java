package com.enonic.cms.core.search.facet.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

public class FacetRanges
    extends AbstractFacetModel
{
    @XmlElement(name = "range")
    private Set<FacetRange> ranges = new LinkedHashSet<FacetRange>();

    public void addFacetRange( FacetRange facetRange )
    {
        this.ranges.add( facetRange );
    }

    public FacetRanges()
    {
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
