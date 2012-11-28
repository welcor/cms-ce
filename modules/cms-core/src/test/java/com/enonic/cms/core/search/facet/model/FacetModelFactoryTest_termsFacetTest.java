package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FacetModelFactoryTest_termsFacetTest
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    @Test
    public void simple_model()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <count>10</count>\n" +
            "        <all-terms>true</all-terms>\n" +
            "        <indices>data/activity, data/something</indices>\n" +
            "        <orderby>hits</orderby>\n" +
            "    </terms>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();
        assertTrue( next instanceof TermsFacetModel );

        TermsFacetModel termsFacetModel = (TermsFacetModel) next;
        assertTrue( termsFacetModel.getAllTerms() );
        assertEquals( "data/activity, data/something", termsFacetModel.getIndices() );
        assertEquals( "hits", termsFacetModel.getOrderby() );
    }

    @Test
    public void missing_field()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <count>10</count>\n" +
            "        <all-terms>true</all-terms>\n" +
            "        <orderby>hits</orderby>\n" +
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
            assertTrue( e.getMessage(), e.getMessage().contains( "'indices' must be set" ) );
            exceptionThrown = true;
        }

        assertTrue( exceptionThrown );
    }

    @Test
    public void unsupported_orderby()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <terms name=\"myFacetName\">\n" +
            "        <count>10</count>\n" +
            "        <all-terms>true</all-terms>\n" +
            "        <indices>data/activity, data/something</indices>\n" +
            "        <orderby>max</orderby>\n" +
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
            assertTrue( e.getMessage(), e.getMessage().contains( "Unsupported orderby-value" ) );
            exceptionThrown = true;
        }

        assertTrue( exceptionThrown );
    }


}
