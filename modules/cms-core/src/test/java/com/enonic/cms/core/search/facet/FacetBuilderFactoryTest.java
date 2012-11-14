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
        query.setFacetDefinition( facetXml );

        final Collection<AbstractFacetBuilder> abstractFacetBuilders = factory.buildFacetBuilder( query );

        assertTrue( abstractFacetBuilders.size() == 0 );
    }

    @Test
    public void testFacets_singleTermsFacet()
    {
        String facetXml = "<facets>\n" +
            "    <terms>\n" +
            "        <name>myFacetName</name>\n" +
            "        <size>10</size>\n" +
            "        <field>termsFacetField</field>\n" +
            "    </terms>\n" +
            "</facets>";

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setFacetDefinition( facetXml );

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
            "    <terms>\n" +
            "        <name>myFacet1</name>\n" +
            "        <size>10</size>\n" +
            "        <field>fieldName</field>\n" +
            "    </terms>\n" +
            "    <terms>\n" +
            "        <name>myFacet2</name>\n" +
            "        <size>10</size>\n" +
            "        <field>fieldName</field>\n" +
            "    </terms>\n" +
            "    <terms>\n" +
            "        <name>myFacet3</name>\n" +
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
