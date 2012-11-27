package com.enonic.cms.core.search.result;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

public class RangeFacetResultSetXmlCreatorTest
    extends FacetResultSetTestBase
{

    @Test
    public void numeric_range_facet()
    {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root>\n" +
            "  <facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "      <range hits=\"1\" to=\"10\" min=\"5.0\" mean=\"5.0\" max=\"5.0\" />\n" +
            "      <range hits=\"3\" from=\"10\" to=\"20\" min=\"11.0\" mean=\"14.3\" max=\"19.0\" />\n" +
            "      <range hits=\"5\" from=\"20\" to=\"30\" min=\"21.0\" mean=\"24.5\" max=\"29.2\" />\n" +
            "      <range hits=\"1\" from=\"30\" min=\"122.0\" mean=\"122.0\" max=\"122.0\" />\n" +
            "    </ranges>\n" +
            "  </facets>\n" +
            "</root>";

        FacetResultSetXmlCreator resultSetXmlCreator = new FacetResultSetXmlCreator();

        Document doc = new Document( new Element( "root" ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        RangeFacetResultSet rangeFacetResultSet = new RangeFacetResultSet();

        rangeFacetResultSet.setName( "myRangeFacet" );
        rangeFacetResultSet.addResult( createResultEntry( null, "10", 1L, new Double( 5 ), new Double( 5 ), new Double( 5 ) ) );
        rangeFacetResultSet.addResult( createResultEntry( "10", "20", 3L, new Double( 11 ), new Double( 14.3 ), new Double( 19 ) ) );
        rangeFacetResultSet.addResult( createResultEntry( "20", "30", 5L, new Double( 21 ), new Double( 24.5 ), new Double( 29.2 ) ) );
        rangeFacetResultSet.addResult( createResultEntry( "30", null, 1L, new Double( 122 ), new Double( 122 ), new Double( 122 ) ) );

        facetsResultSet.addFacetResultSet( rangeFacetResultSet );

        resultSetXmlCreator.addFacetResultXml( doc, facetsResultSet );

        compareIgnoreWhitespacesAndLinebreaks( expected, doc );
    }

    @Test
    public void date_range_facet()
    {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root>\n" +
            "  <facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "      <range hits=\"1\" to=\"2000-01-02 00:00:00\" />\n" +
            "      <range hits=\"3\" from=\"2001-01-02 01:00:00\" />\n" +
            "    </ranges>\n" +
            "  </facets>\n" +
            "</root>";

        FacetResultSetXmlCreator resultSetXmlCreator = new FacetResultSetXmlCreator();

        Document doc = new Document( new Element( "root" ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        RangeFacetResultSet rangeFacetResultSet = new RangeFacetResultSet();

        rangeFacetResultSet.setName( "myRangeFacet" );
        rangeFacetResultSet.addResult( createResultEntry( null, "2000-01-01T23:00:00.000Z", 1L, null, null, null ) );
        rangeFacetResultSet.addResult( createResultEntry( "2001-01-02T00:00:00.000Z", null, 3L, null, null, null ) );

        facetsResultSet.addFacetResultSet( rangeFacetResultSet );

        resultSetXmlCreator.addFacetResultXml( doc, facetsResultSet );

        compareIgnoreWhitespacesAndLinebreaks( expected, doc );
    }


    private RangeFacetResultEntry createResultEntry( final String from, final String to, final Long count, final Double min,
                                                     final Double mean, final Double max )
    {
        RangeFacetResultEntry resultEntry = new RangeFacetResultEntry();
        resultEntry.setFrom( from );
        resultEntry.setTo( to );
        resultEntry.setCount( count );
        resultEntry.setMin( min );
        resultEntry.setMax( max );
        resultEntry.setMean( mean );
        return resultEntry;
    }

}
