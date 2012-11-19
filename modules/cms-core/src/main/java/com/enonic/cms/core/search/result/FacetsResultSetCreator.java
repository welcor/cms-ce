package com.enonic.cms.core.search.result;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

public class FacetsResultSetCreator
{

    private final TermFacetResultSetCreator1 termFacetResultSetCreator1 = new TermFacetResultSetCreator1();

    private final RangeFacetResultSetCreator rangeFacetResultSetCreator = new RangeFacetResultSetCreator();

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
                facetsResultSet.addFacetResultSet( termFacetResultSetCreator1.createTermFacetResultSet( facetName, (TermsFacet) facet ) );
            }
            else if ( facet instanceof RangeFacet )
            {
                facetsResultSet.addFacetResultSet( rangeFacetResultSetCreator.createRangeFacetResultSet( facetName, (RangeFacet) facet ) );
            }

        }

        return facetsResultSet;
    }


}
