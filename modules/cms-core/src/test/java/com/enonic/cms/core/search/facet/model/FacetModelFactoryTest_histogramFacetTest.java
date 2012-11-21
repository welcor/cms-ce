package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FacetModelFactoryTest_histogramFacetTest
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    @Test
    public void simple_model()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <histogram name=\"myHistogram\">\n" +
            "        <count>10</count>\n" +
            "        <index>data/activity</index>\n" +
            "        <interval>100</interval>\n" +
            "    </histogram>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();

        assertTrue( next instanceof HistogramFacetModel );

        HistogramFacetModel histogramFacetModel = (HistogramFacetModel) next;

        histogramFacetModel.validate();

        assertEquals( "data/activity", histogramFacetModel.getIndex() );
        assertEquals( 100L, histogramFacetModel.getInterval() );
        assertEquals( new Integer( 10 ), histogramFacetModel.getCount() );
    }

    @Test
    public void missing_interval()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <histogram name=\"myHistogram\">\n" +
            "        <count>10</count>\n" +
            "        <index>data/activity</index>\n" +
            "    </histogram>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();

        assertTrue( next instanceof HistogramFacetModel );

        HistogramFacetModel histogramFacetModel = (HistogramFacetModel) next;

        assertExceptionContainingString( histogramFacetModel, "'interval' must be set" );
    }

    private void assertExceptionContainingString( final HistogramFacetModel histogramFacetModel, final String containsString )
    {
        boolean exceptionThrowed = false;

        try
        {
            histogramFacetModel.validate();
        }
        catch ( Exception e )
        {
            exceptionThrowed = true;
            assertTrue( "Message " + e.getMessage(), e.getMessage().contains( containsString ) );
        }

        assertTrue( exceptionThrowed );
    }


}
