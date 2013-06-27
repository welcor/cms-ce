/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

public class FacetRangeNumericValue
    extends FacetRangeValue
{
    Double value;

    public FacetRangeNumericValue( final Double value )
    {
        this.value = value;
    }

    public String getStringValue()
    {
        return value != null ? value.toString() : null;
    }

    public FacetRangeNumericValue()
    {
    }
}
