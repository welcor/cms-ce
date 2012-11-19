package com.enonic.cms.core.search.result;

import java.util.List;

import org.elasticsearch.search.facet.range.RangeFacet;

public class RangeFacetResultSetCreator
{

    protected FacetResultSet createRangeFacetResultSet( final String facetName, final RangeFacet facet )
    {
        RangeFacetResultSet rangeFacetResultSet = new RangeFacetResultSet();

        rangeFacetResultSet.setName( facetName );

        final List<RangeFacet.Entry> entries = facet.getEntries();

        for ( RangeFacet.Entry entry : entries )
        {
            rangeFacetResultSet.addResult( createRangeFacetResultEntry( entry ) );
        }

        return rangeFacetResultSet;
    }

    private RangeFacetResultEntry createRangeFacetResultEntry( RangeFacet.Entry entry )
    {
        RangeFacetResultEntry rangeFacetResultEntry = new RangeFacetResultEntry();

        rangeFacetResultEntry.setFrom( entry.getFromAsString() );
        rangeFacetResultEntry.setTo( entry.getToAsString() );
        rangeFacetResultEntry.setCount( entry.getCount() );

        return rangeFacetResultEntry;
    }

}
