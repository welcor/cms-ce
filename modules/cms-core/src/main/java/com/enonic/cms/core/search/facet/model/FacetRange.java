package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlElement;

public class FacetRange
    extends AbstractFacetModel
{
    private FacetRangeValue fromRangeValue;

    private FacetRangeValue toRangeValue;

    public FacetRange( final String from, final String to )
    {
        setFrom( from );
        setTo( to );
    }

    // JAXB-necessary
    public FacetRange()
    {
    }

    @XmlElement(name = "from")
    public void setFrom( final String from )
    {
        this.fromRangeValue = FacetRangeValueFactory.createFacetRangeValue( from );
    }

    @XmlElement(name = "to")
    public void setTo( final String to )
    {
        this.toRangeValue = FacetRangeValueFactory.createFacetRangeValue( to );
    }

    // JAXB-necessary
    public String getFrom()
    {
        return fromRangeValue != null ? fromRangeValue.getStringValue() : null;
    }

    // JAXB-necessary
    public String getTo()
    {
        return toRangeValue != null ? toRangeValue.getStringValue() : null;
    }

    public FacetRangeValue getFromRangeValue()
    {
        return fromRangeValue;
    }

    public FacetRangeValue getToRangeValue()
    {
        return toRangeValue;
    }

    protected boolean isNumericRange()
    {
        return fromRangeValue instanceof FacetRangeNumericValue || toRangeValue instanceof FacetRangeNumericValue;
    }

    @Override
    public void validate()
    {
        Class fromClazz = fromRangeValue != null ? fromRangeValue.getClass() : null;
        Class toClazz = toRangeValue != null ? toRangeValue.getClass() : null;

        if ( fromClazz == null && toClazz == null )
        {
            throw new IllegalArgumentException( "Both from and to - range values empty" );
        }

        if ( fromClazz != null && toClazz != null && !fromClazz.equals( toClazz ) )
        {
            throw new IllegalArgumentException(
                "Incompatible values in range - from: " + fromRangeValue.getStringValue() + " (" + fromClazz + ")" +
                    " - to: " +
                    toRangeValue.getStringValue() + " (" + toClazz + ")" );
        }
    }
}