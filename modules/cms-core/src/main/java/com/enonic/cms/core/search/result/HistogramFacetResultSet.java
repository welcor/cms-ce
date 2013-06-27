/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.Set;

import com.google.common.collect.Sets;

public class HistogramFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    Set<HistogramFacetResultEntry> resultEntries = Sets.newLinkedHashSet();

    @Override
    public FacetResultType getFacetResultType()
    {
        return FacetResultType.HISTOGRAM;
    }


    public void addResult( HistogramFacetResultEntry result )
    {
        resultEntries.add( result );
    }

    public Set<HistogramFacetResultEntry> getResultEntries()
    {
        return resultEntries;
    }

    public void setResultEntries( final Set<HistogramFacetResultEntry> resultEntries )
    {
        this.resultEntries = resultEntries;
    }
}
