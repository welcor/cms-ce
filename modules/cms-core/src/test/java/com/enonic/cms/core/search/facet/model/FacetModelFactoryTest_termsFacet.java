package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FacetModelFactoryTest_termsFacet
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    @Test
    public void simple_model()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <size>10</size>\n" +
            "        <all_terms>true</all_terms>\n" +
            "        <fields>data/activity, data/something</fields>\n" +
            "        <order>count</order>\n" +
            "    </terms>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();
        assertTrue( next instanceof TermsFacetModel );

        TermsFacetModel termsFacetModel = (TermsFacetModel) next;
        assertTrue( termsFacetModel.getAllTerms() );
        assertEquals( "data/activity, data/something", termsFacetModel.getFields() );
        assertEquals( "count", termsFacetModel.getOrder() );
    }

    @Test
    public void missing_field()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <size>10</size>\n" +
            "        <all_terms>true</all_terms>\n" +
            "        <order>count</order>\n" +
            "    </terms>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();
        assertTrue( next instanceof TermsFacetModel );

        TermsFacetModel termsFacetModel = (TermsFacetModel) next;

        boolean exceptionThrown = false;

        try
        {
            termsFacetModel.validate();
        }
        catch ( Exception e )
        {
            assertTrue( e.getMessage().contains( "Field or fields must be set" ) );
            exceptionThrown = true;
        }

        assertTrue( exceptionThrown );
    }


}
