package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FacetModelFactoryTest_dateHistogramFacetTest
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    @Test
    public void simple_model()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <date-histogram name=\"myHistogram\">\n" +
            "        <count>10</count>\n" +
            "        <index>data/activity</index>\n" +
            "        <interval>month</interval>\n" +
            "    </date-histogram>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();

        assertTrue( next instanceof DateHistogramFacetModel );

        DateHistogramFacetModel dateHistogramFacetModel = (DateHistogramFacetModel) next;

        dateHistogramFacetModel.validate();

        assertEquals( "data/activity", dateHistogramFacetModel.getIndex() );
        assertEquals( "month", dateHistogramFacetModel.getInterval() );
        assertEquals( new Integer( 10 ), dateHistogramFacetModel.getCount() );
    }
}
