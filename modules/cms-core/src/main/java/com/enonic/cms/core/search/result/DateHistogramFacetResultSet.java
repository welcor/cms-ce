package com.enonic.cms.core.search.result;

import java.util.Set;

import com.google.common.collect.Sets;

public class DateHistogramFacetResultSet
    extends AbstractFacetResultSet
    implements FacetResultSet
{
    Set<DateHistogramFacetResultEntry> resultEntries = Sets.newLinkedHashSet();

    @Override
    public FacetResultType getFacetResultType()
    {
        return FacetResultType.DATE_HISTOGRAM;
    }

    public void addResult( DateHistogramFacetResultEntry result )
    {
        resultEntries.add( result );
    }

    public Set<DateHistogramFacetResultEntry> getResultEntries()
    {
        return resultEntries;
    }

    public void setResultEntries( final Set<DateHistogramFacetResultEntry> resultEntries )
    {
        this.resultEntries = resultEntries;
    }
}
