/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.Set;

import com.google.common.collect.Sets;

public class TermsStatsFacetResultSet
    extends AbstractFacetResultSet
{
    private Long missing;

    private Set<TermsStatsFacetResultEntry> results = Sets.newLinkedHashSet();

    public void addResult( TermsStatsFacetResultEntry termsStatsFacetResultEntry )
    {
        results.add( termsStatsFacetResultEntry );
    }

    public Long getMissing()
    {
        return missing;
    }

    public void setMissing( final Long missing )
    {
        this.missing = missing;
    }

    @Override
    public FacetResultType getFacetResultType()
    {
        return FacetResultType.TERMS_STATS;
    }

    public Set<TermsStatsFacetResultEntry> getResults()
    {
        return results;
    }
}
