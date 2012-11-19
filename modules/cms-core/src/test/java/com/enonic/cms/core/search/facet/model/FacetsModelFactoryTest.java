package com.enonic.cms.core.search.facet.model;

import org.junit.Test;

import com.enonic.cms.core.search.facet.FacetQueryException;

import static org.junit.Assert.*;

public class FacetsModelFactoryTest
{
    private FacetsModelFactory facetsModelFactory = new FacetsModelFactory();

    @Test
    public void testBuildRangeFacetModel_numericRanges()
        throws Exception
    {
        final String rangeFacetName = "myRangeFacet";
        String xml = "<facets>\n" +
            "    <range name=\"myRangeFacet\">\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "                <to>49</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>50</from>\n" +
            "                <to>100</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>101</from>\n" +
            "                <to>199</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>201</from>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>rangeField</field>\n" +
            "    </range>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        RangeFacetModel rangeFacetModel = assertAndGetRangeFacetModel( facetsModel );

        rangeFacetModel.validate();

        assertEquals( rangeFacetName, rangeFacetModel.getName() );

        final FacetRanges facetRanges = rangeFacetModel.getFacetRanges();
        assertNotNull( facetRanges );

        for ( final FacetRange facetRange : facetRanges.getRanges() )
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
            "    <range>\n" +
            "        <name>myRangeFacet</name>\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "                <to>xx</to>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>rangeField</field>\n" +
            "    </range>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        RangeFacetModel rangeFacetModel = null;
        rangeFacetModel = assertAndGetRangeFacetModel( facetsModel );

    }


    @Test
    public void testBuildRangeFacetModel_mixedDateAndNumbers()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <range name=\"myRangeFacet\">\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "                <to>49</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>1975-08-01</from>\n" +
            "                <to>100</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>101</from>\n" +
            "                <to>199</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>201</from>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>rangeField</field>\n" +
            "    </range>\n" +
            "</facets>\n";

        final FacetsModel facetsModel = facetsModelFactory.buildFromXml( xml );

        RangeFacetModel rangeFacetModel = assertAndGetRangeFacetModel( facetsModel );

        assertExceptionContainingString( rangeFacetModel, "Incompatible values in range" );
    }


    @Test
    public void testBuildRangeFacetModel_startAndEndIsNull()
        throws Exception
    {
        String xml = "<facets>\n" +
            "    <range name=\"myRangeFacet\">\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>1975-08-01</from>\n" +
            "                <to>100</to>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>rangeField</field>\n" +
            "    </range>\n" +
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
