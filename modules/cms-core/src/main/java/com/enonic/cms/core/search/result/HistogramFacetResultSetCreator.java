/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.List;

import org.elasticsearch.search.facet.histogram.HistogramFacet;

public class HistogramFacetResultSetCreator
    extends AbstractFacetResultSetCreator
{

    protected HistogramFacetResultSet create( final String facetName, final HistogramFacet facet )
    {
        HistogramFacetResultSet histogramFacetResultSet = new HistogramFacetResultSet();

        histogramFacetResultSet.setName( facetName );

        final List<? extends HistogramFacet.Entry> entries = facet.getEntries();

        for ( HistogramFacet.Entry entry : entries )
        {
            HistogramFacetResultEntry result = new HistogramFacetResultEntry();
            result.setCount( entry.getCount() );
            result.setKey( entry.getKey() );
            result.setMax( getValueIfNumber( entry.getMax() ) );
            result.setMean( getValueIfNumber( entry.getMean() ) );
            result.setMin( getValueIfNumber( entry.getMin() ) );
            result.setTotal( getValueIfNumber( entry.getTotal() ) );

            histogramFacetResultSet.addResult( result );
        }

        return histogramFacetResultSet;
    }

}
