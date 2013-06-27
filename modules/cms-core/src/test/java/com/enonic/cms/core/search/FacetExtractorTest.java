/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.query.QueryFacet;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class FacetExtractorTest
{

    @Test
    public void testGetStatisticalFacet()
    {
        final Facet statisticalFacet = createStatisticalFacet( "test" );
        final Facets facets = createFacets( statisticalFacet );

        SearchResponse response = Mockito.mock( SearchResponse.class );
        Mockito.when( response.facets() ).thenReturn( facets );

        assertNull( FacetExtractor.getStatisticalFacet( response, "nonExisting" ) );
        assertNotNull( FacetExtractor.getStatisticalFacet( response, "test" ) );
    }

    @Test(expected = IndexException.class)
    public void testNotStatistical()
    {
        final Facet queryFacet = createQueryFacet( "test" );
        final Facets facets = createFacets( queryFacet );

        SearchResponse response = Mockito.mock( SearchResponse.class );
        Mockito.when( response.facets() ).thenReturn( facets );

        final StatisticalFacet test = FacetExtractor.getStatisticalFacet( response, "test" );
    }

    private Facets createFacets( final Facet facet )
    {
        final Facets facets = new Facets()
        {
            @Override
            public List<Facet> facets()
            {
                return Lists.newArrayList( facet );
            }

            @Override
            public Map<String, Facet> getFacets()
            {
                return null;
            }

            @Override
            public Map<String, Facet> facetsAsMap()
            {
                return null;
            }

            @Override
            public <T extends Facet> T facet( final Class<T> facetType, final String name )
            {
                return null;
            }

            @Override
            public <T extends Facet> T facet( final String name )
            {
                return null;
            }

            @Override
            public Iterator<Facet> iterator()
            {
                return null;
            }
        };
        return facets;
    }


    private Facet createQueryFacet( final String name )
    {

        Facet facet = new QueryFacet()
        {
            @Override
            public long count()
            {
                return 0;
            }

            @Override
            public long getCount()
            {
                return 0;
            }

            @Override
            public String name()
            {
                return name;
            }

            @Override
            public String getName()
            {
                return name();
            }

            @Override
            public String type()
            {
                return null;
            }

            @Override
            public String getType()
            {
                return null;
            }
        };
        return facet;
    }

    private Facet createStatisticalFacet( final String name )
    {
        Facet facet = new StatisticalFacet()
        {
            @Override
            public long count()
            {
                return 10;
            }

            @Override
            public long getCount()
            {
                return 10;
            }

            @Override
            public double total()
            {
                return 20;
            }

            @Override
            public double getTotal()
            {
                return 20;
            }

            @Override
            public double sumOfSquares()
            {
                return 30;
            }

            @Override
            public double getSumOfSquares()
            {
                return 30;
            }

            @Override
            public double mean()
            {
                return 30;
            }

            @Override
            public double getMean()
            {
                return 0;
            }

            @Override
            public double min()
            {
                return 0;
            }

            @Override
            public double getMin()
            {
                return 0;
            }

            @Override
            public double max()
            {
                return 0;
            }

            @Override
            public double getMax()
            {
                return 0;
            }

            @Override
            public double variance()
            {
                return 0;
            }

            @Override
            public double getVariance()
            {
                return 0;
            }

            @Override
            public double stdDeviation()
            {
                return 0;
            }

            @Override
            public double getStdDeviation()
            {
                return 0;
            }

            @Override
            public String name()
            {
                return name;
            }

            @Override
            public String getName()
            {
                return name();
            }

            @Override
            public String type()
            {
                return null;
            }

            @Override
            public String getType()
            {
                return null;
            }
        };

        return facet;
    }


}
