/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;

public class FacetExtractor
{
    public static StatisticalFacet getStatisticalFacet( SearchResponse response, String facetName )
    {
        Facet facet = getFacet( response, facetName );

        if ( facet == null )
        {
            return null;
        }

        if ( facet instanceof StatisticalFacet )
        {
            return (StatisticalFacet) facet;
        }

        throw new IndexException( "Facet '" + facetName + "' not of expected type Statistical" );
    }

    private static Facet getFacet( SearchResponse response, String facetName )
    {
        if ( response == null )
        {
            return null;
        }

        Facets facets = response.getFacets();

        if ( facets == null )
        {
            return null;
        }

        for ( Facet facet : facets.facets() )
        {
            if ( facetName.equalsIgnoreCase( facet.getName() ) )
            {
                return facet;
            }
        }

        return null;
    }

}
