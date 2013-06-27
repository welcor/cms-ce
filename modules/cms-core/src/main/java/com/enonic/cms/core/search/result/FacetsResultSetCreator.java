/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;
import org.elasticsearch.search.facet.histogram.HistogramFacet;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;

public class FacetsResultSetCreator
{
    private final TermFacetResultSetCreator termFacetResultSetCreator = new TermFacetResultSetCreator();

    private final RangeFacetResultSetCreator rangeFacetResultSetCreator = new RangeFacetResultSetCreator();

    private final HistogramFacetResultSetCreator histogramFacetResultSetCreator = new HistogramFacetResultSetCreator();

    private final DateHistogramFacetResultSetCreator dateHistogramFacetResultSetCreator = new DateHistogramFacetResultSetCreator();

    private final TermsStatsFacetResultSetCreator termsStatsFacetResultSetCreator = new TermsStatsFacetResultSetCreator();

    public FacetsResultSet createResultSet( SearchResponse searchResponse )
    {
        Facets facets = searchResponse.getFacets();

        if ( facets == null )
        {
            return null;
        }

        FacetsResultSet facetsResultSet = new FacetsResultSet();

        final Map<String, Facet> facetsMap = facets.getFacets();

        for ( String facetName : facetsMap.keySet() )
        {
            final Facet facet = facetsMap.get( facetName );

            if ( facet instanceof TermsFacet )
            {
                facetsResultSet.addFacetResultSet( termFacetResultSetCreator.create( facetName, (TermsFacet) facet ) );
            }
            else if ( facet instanceof RangeFacet )
            {
                facetsResultSet.addFacetResultSet( rangeFacetResultSetCreator.create( facetName, (RangeFacet) facet ) );
            }
            else if ( facet instanceof HistogramFacet )
            {
                facetsResultSet.addFacetResultSet( histogramFacetResultSetCreator.create( facetName, (HistogramFacet) facet ) );
            }
            else if ( facet instanceof DateHistogramFacet )
            {
                facetsResultSet.addFacetResultSet( dateHistogramFacetResultSetCreator.create( facetName, (DateHistogramFacet) facet ) );
            }
            else if ( facet instanceof TermsStatsFacet )
            {
                facetsResultSet.addFacetResultSet( termsStatsFacetResultSetCreator.create( facetName, (TermsStatsFacet) facet ) );
            }
        }

        return facetsResultSet;
    }
}
