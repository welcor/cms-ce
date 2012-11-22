package com.enonic.cms.core.search.result;

import java.util.List;

import org.elasticsearch.search.facet.terms.TermsFacet;

public class TermFacetResultSetCreator
{
    public TermFacetResultSetCreator()
    {
    }

    protected FacetResultSet create( final String facetName, final TermsFacet facet )
    {
        TermsFacetResultSet termsFacetResultSet = new TermsFacetResultSet();
        termsFacetResultSet.setName( facetName );
        termsFacetResultSet.setTotal( facet.getTotalCount() );
        termsFacetResultSet.setMissing( facet.getMissingCount() );
        termsFacetResultSet.setOther( facet.getOtherCount() );

        final List<? extends TermsFacet.Entry> entries = facet.entries();
        for ( TermsFacet.Entry entry : entries )
        {
            termsFacetResultSet.addResult( entry.getTerm(), entry.getCount() );
        }

        return termsFacetResultSet;
    }
}