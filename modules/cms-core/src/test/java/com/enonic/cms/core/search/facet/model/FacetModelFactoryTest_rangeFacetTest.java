/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import com.enonic.cms.core.search.facet.FacetQueryException;

import static org.junit.Assert.*;

public class FacetModelFactoryTest_rangeFacetTest
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();


    @Test
    public void plain_xml()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "        <range to=\"4\"/>\n" +
            "        <index>rangeField</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        assertAndGetRangeFacetModel( facetsModel );
    }

    @Test
    public void testBuildRangeFacetModel_numericRanges()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range to=\"45\"/>\n" +
            "            <range from=\"50\" to=\"100\"/>\n" +
            "            <range from=\"100\" to=\"200\"/>\n" +
            "            <range from=\"200\" />\n" +
            "        <index>rangeField</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        RangeFacetModel rangeFacetModel = assertAndGetRangeFacetModel( facetsModel );

        rangeFacetModel.validate();

        assertEquals( "myRangeFacet", rangeFacetModel.getName() );

        for ( final FacetRange facetRange : rangeFacetModel.getRanges() )
        {
            assertTrue( facetRange.getFromRangeValue() == null || facetRange.getFromRangeValue() instanceof FacetRangeNumericValue );
            assertTrue( facetRange.getToRangeValue() == null || facetRange.getToRangeValue() instanceof FacetRangeNumericValue );
        }

    }

    @Test(expected = FacetQueryException.class)
    public void testBuildRangeFacetModel_illegal_range_value()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range to=\"xx\"/>\n" +
            "            <range from=\"50\" to=\"100\"/>\n" +
            "            <range from=\"100\" to=\"200\"/>\n" +
            "            <range from=\"200\" />\n" +
            "        <index>rangeField</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        assertAndGetRangeFacetModel( facetsModel );
    }


    @Test
    public void testBuildRangeFacetModel_mixedDateAndNumbers()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range to=\"2001-01-01\"/>\n" +
            "            <range from=\"50\" to=\"100\"/>\n" +
            "            <range from=\"100\" to=\"200\"/>\n" +
            "            <range from=\"200\" />\n" +
            "        <index>rangeField</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        RangeFacetModel rangeFacetModel = assertAndGetRangeFacetModel( facetsModel );

        assertExceptionContainingString( rangeFacetModel, "All range-values" );
    }


    @Test
    public void testBuildRangeFacetModel_startAndEndIsNull()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range/>\n" +
            "            <range from=\"50\" to=\"100\"/>\n" +
            "            <range from=\"100\" to=\"200\"/>\n" +
            "            <range from=\"200\" />\n" +
            "        <index>rangeField</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        RangeFacetModel rangeFacetModel = assertAndGetRangeFacetModel( facetsModel );

        assertExceptionContainingString( rangeFacetModel, "range values empty" );
    }

    private void assertExceptionContainingString( final RangeFacetModel rangeFacetModel, final String containsString )
    {
        boolean exceptionThrowed = false;

        try
        {
            rangeFacetModel.validate();
        }
        catch ( Exception e )
        {
            exceptionThrowed = true;
            assertTrue( "Message " + e.getMessage(), e.getMessage().contains( containsString ) );
        }

        assertTrue( exceptionThrowed );
    }

    private RangeFacetModel assertAndGetRangeFacetModel( final FacetsModel facetsModel )
    {
        assertNotNull( facetsModel.getFacetModels() );
        assertEquals( 1, facetsModel.getFacetModels().size() );

        final FacetModel facetModel = facetsModel.iterator().next();
        assertTrue( facetModel instanceof RangeFacetModel );
        return (RangeFacetModel) facetModel;
    }

}
