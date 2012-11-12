package com.enonic.cms.core.search.query.factory;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.query.factory.facet.FacetBuilderFactory;

import static org.junit.Assert.*;

public class FacetBuilderFactoryTest
{
    FacetBuilderFactory factory = new FacetBuilderFactory();


    @Test
    public void testEmptyFacet()
    {
        String facetXml = "<facets/>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacetDefinition( facetXml );

        final Collection<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( abstractFacetBuilders.size() == 0 );
    }

    @Test
    public void testFacets_singleTermsFacet()
    {
        String facetXml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <size>10</size>\n" +
            "        <field>fieldName</field>\n" +
            "    </terms>\n" +
            "</facets>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacetDefinition( facetXml );

        final Set<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( abstractFacetBuilders.size() == 1 );

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
            "        <size>10</size>\n" +
            "        <field>fieldName</field>\n" +
            "    </terms>\n" +
            "    <terms name=\"myFacet2\">\n" +
            "        <size>10</size>\n" +
            "        <field>fieldName</field>\n" +
            "    </terms>\n" +
            "    <terms name=\"myFacet3\">\n" +
            "        <size>10</size>\n" +
            "        <field>fieldName</field>\n" +
            "    </terms>\n" +
            "</facets>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacetDefinition( facetXml );

        final Set<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( abstractFacetBuilders.size() == 3 );

        for ( AbstractFacetBuilder builder : abstractFacetBuilders )
        {
            assertTrue( builder instanceof TermsFacetBuilder );
        }
    }


}
