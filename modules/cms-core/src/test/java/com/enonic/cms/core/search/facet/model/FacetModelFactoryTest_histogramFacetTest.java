package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

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
            "    </histogram>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();


    }

}
