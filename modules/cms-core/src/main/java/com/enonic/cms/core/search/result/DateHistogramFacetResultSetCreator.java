/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.List;

import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;

public class DateHistogramFacetResultSetCreator
    extends AbstractFacetResultSetCreator
{

    protected DateHistogramFacetResultSet create( final String facetName, final DateHistogramFacet facet )
    {
        DateHistogramFacetResultSet dateHistogramFacetResultSet = new DateHistogramFacetResultSet();

        dateHistogramFacetResultSet.setName( facetName );

        final List<? extends DateHistogramFacet.Entry> entries = facet.getEntries();

        for ( DateHistogramFacet.Entry entry : entries )
        {
            DateHistogramFacetResultEntry result = new DateHistogramFacetResultEntry();
            result.setCount( entry.getCount() );
            result.setTime( entry.getTime() );
            result.setMax( getValueIfNumber( entry.getMax() ) );
            result.setMean( getValueIfNumber( entry.getMean() ) );
            result.setMin( getValueIfNumber( entry.getMin() ) );
            result.setTotal( getValueIfNumber( entry.getTotal() ) );

            dateHistogramFacetResultSet.addResult( result );
        }

        return dateHistogramFacetResultSet;
    }

}
