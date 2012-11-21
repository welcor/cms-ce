package com.enonic.cms.core.search.result;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.histogram.HistogramFacet;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

public class FacetsResultSetCreator
{

    private final TermFacetResultSetCreator termFacetResultSetCreator = new TermFacetResultSetCreator();

    private final RangeFacetResultSetCreator rangeFacetResultSetCreator = new RangeFacetResultSetCreator();

    private final HistogramFacetResultSetCreator histogramFacetResultSetCreator = new HistogramFacetResultSetCreator();

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
                facetsResultSet.addFacetResultSet( termFacetResultSetCreator.createTermFacetResultSet( facetName, (TermsFacet) facet ) );
            }
            else if ( facet instanceof RangeFacet )
            {
                facetsResultSet.addFacetResultSet( rangeFacetResultSetCreator.createRangeFacetResultSet( facetName, (RangeFacet) facet ) );
            }
            else if ( facet instanceof HistogramFacet )
            {
                facetsResultSet.addFacetResultSet(
                    histogramFacetResultSetCreator.createHistogramFacetResultSet( facetName, (HistogramFacet) facet ) );
            }

        }

        return facetsResultSet;
    }


}
