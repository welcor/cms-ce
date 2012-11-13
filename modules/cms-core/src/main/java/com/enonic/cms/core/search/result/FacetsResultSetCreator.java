package com.enonic.cms.core.search.result;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.terms.TermsFacet;

public class FacetsResultSetCreator
{

    public FacetsResultSet createResultSet( SearchResponse searchResponse )
    {
        //System.out.println( searchResponse );

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

            if ( "terms".equals( facet.getType() ) )
            {
                createTermFacetResultSet( facetsResultSet, facetName, (TermsFacet) facet );
            }

        }

        return facetsResultSet;
    }

    private void createTermFacetResultSet( final FacetsResultSet facetsResultSet, final String facetName, final TermsFacet facet )
    {
        TermsFacetResultSet termsFacetResultSet = new TermsFacetResultSet();
        termsFacetResultSet.setName( facetName );

        TermsFacet termFacet = (TermsFacet) facet;

        termsFacetResultSet.setTotal( termFacet.getTotalCount() );
        termsFacetResultSet.setMissing( termFacet.getMissingCount() );
        // TODO Set the correct count-variables
        termsFacetResultSet.setRequiredSize( termFacet.getOtherCount() );

        final List<? extends TermsFacet.Entry> entries = termFacet.entries();
        for ( TermsFacet.Entry entry : entries )
        {
            termsFacetResultSet.addResult( entry.getTerm(), entry.getCount() );
        }

        facetsResultSet.addFacetResultSet( termsFacetResultSet );
    }


}
