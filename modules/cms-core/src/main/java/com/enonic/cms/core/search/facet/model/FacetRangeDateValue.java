/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import org.joda.time.ReadableDateTime;

public class FacetRangeDateValue
    extends FacetRangeValue
{
    ReadableDateTime value;

    public FacetRangeDateValue( final ReadableDateTime value )
    {
        this.value = value;
    }

    @Override
    public String getStringValue()
    {
        return value != null ? this.value.toString() : null;
    }

    public FacetRangeDateValue()
    {
    }
}
