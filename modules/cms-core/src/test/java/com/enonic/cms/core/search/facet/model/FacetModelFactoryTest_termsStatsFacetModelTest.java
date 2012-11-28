package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FacetModelFactoryTest_termsStatsFacetModelTest
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    @Test
    public void simple_model()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <terms-stats name=\"myFacetName\">\n" +
            "        <count>10</count>\n" +
            "        <index>data/activity</index>\n" +
            "        <value-index>data/hours</value-index>\n" +
            "        <orderby>hits</orderby>\n" +
            "    </terms-stats>\n" +
            "</facets>";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        final FacetModel next = facetsModel.iterator().next();
        assertTrue( next instanceof TermsStatsFacetModel );

        TermsStatsFacetModel termsStatsFacetModel = (TermsStatsFacetModel) next;
        assertEquals( "data/activity", termsStatsFacetModel.getIndex() );
        assertEquals( "data/hours", termsStatsFacetModel.getValueIndex() );
        assertEquals( "hits", termsStatsFacetModel.getOrderby() );
    }


}
