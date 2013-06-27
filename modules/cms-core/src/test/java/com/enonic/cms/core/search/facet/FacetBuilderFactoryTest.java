/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

import static org.junit.Assert.*;

public class FacetBuilderFactoryTest
{
    FacetBuilderFactory factory = new FacetBuilderFactory();


    @Test
    public void testEmptyFacet()
    {
        String facetXml = "<facets/>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacets( facetXml );

        final Collection<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( abstractFacetBuilders.size() == 0 );
    }

    @Test
    public void testFacets_singleTermsFacet()
    {
        String facetXml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <count>10</count>\n" +
            "        <indexes>termsFacetField</indexes>\n" +
            "    </terms>\n" +
            "</facets>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacets( facetXml );

        final Set<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( "Should create one facet", abstractFacetBuilders.size() == 1 );

        for ( AbstractFacetBuilder builder : abstractFacetBuilders )
        {
            assertTrue( builder instanceof TermsFacetBuilder );
        }
    }

    @Test
    public void testFacets_multipleTermsFacet()
    {
        String facetXml = "<facets>\n" +
            "    <terms name=\"myFacet1\">\n" +
            "        <count>10</count>\n" +
            "        <indexes>fieldName</indexes>\n" +
            "    </terms>\n" +
            "    <terms name=\"myFacet2\">\n" +
            "        <count>10</count>\n" +
            "        <indexes>fieldName</indexes>\n" +
            "    </terms>\n" +
            "    <terms name=\"myFacet3\">\n" +
            "        <count>10</count>\n" +
            "        <indexes>fieldName</indexes>\n" +
            "    </terms>\n" +
            "</facets>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacets( facetXml );

        final Set<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( abstractFacetBuilders.size() == 3 );

        for ( AbstractFacetBuilder builder : abstractFacetBuilders )
        {
            assertTrue( builder instanceof TermsFacetBuilder );
        }
    }


}
