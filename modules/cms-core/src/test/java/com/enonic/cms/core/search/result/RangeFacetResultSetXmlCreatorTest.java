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
            "    <facet name=\"myRangeFacet\">\n" +
            "      <result to=\"9\">\n" +
            "        <count>1</count>\n" +
            "      </result>\n" +
            "      <result from=\"10\" to=\"19\">\n" +
            "        <count>3</count>\n" +
            "      </result>\n" +
            "      <result from=\"20\" to=\"29\">\n" +
            "        <count>5</count>\n" +
            "      </result>\n" +
            "      <result from=\"30\">\n" +
            "        <count>0</count>\n" +
            "      </result>\n" +
            "    </facet>\n" +
            "  </facets>\n" +
            "</root>\n";

        FacetResultSetXmlCreator resultSetXmlCreator = new FacetResultSetXmlCreator();

        Document doc = new Document( new Element( "root" ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        RangeFacetResultSet rangeFacetResultSet = new RangeFacetResultSet();

        rangeFacetResultSet.setName( "myRangeFacet" );
        rangeFacetResultSet.addResult( createResultEntry( null, "9", 1L ) );
        rangeFacetResultSet.addResult( createResultEntry( "10", "19", 3L ) );
        rangeFacetResultSet.addResult( createResultEntry( "20", "29", 5L ) );
        rangeFacetResultSet.addResult( createResultEntry( "30", null, 0L ) );

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
            "    <facet name=\"myRangeFacet\">\n" +
            "      <result to=\"2000-12-31 23:59:59\">\n" +
            "        <count>1</count>\n" +
            "      </result>\n" +
            "      <result from=\"2001-01-01 00:00:00\">\n" +
            "        <count>3</count>\n" +
            "      </result>\n" +
            "    </facet>\n" +
            "  </facets>\n" +
            "</root>";

        FacetResultSetXmlCreator resultSetXmlCreator = new FacetResultSetXmlCreator();

        Document doc = new Document( new Element( "root" ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        RangeFacetResultSet rangeFacetResultSet = new RangeFacetResultSet();

        rangeFacetResultSet.setName( "myRangeFacet" );
        rangeFacetResultSet.addResult( createResultEntry( null, "2000-12-31T23:59:59", 1L ) );
        rangeFacetResultSet.addResult( createResultEntry( "2001-01-01T00:00:00", null, 3L ) );

        facetsResultSet.addFacetResultSet( rangeFacetResultSet );

        resultSetXmlCreator.addFacetResultXml( doc, facetsResultSet );

        compareIgnoreWhitespacesAndLinebreaks( expected, doc );
    }


    private RangeFacetResultEntry createResultEntry( final String from, final String to, final Long count )
    {
        RangeFacetResultEntry resultEntry = new RangeFacetResultEntry();
        resultEntry.setFrom( from );
        resultEntry.setTo( to );
        resultEntry.setCount( count );
        return resultEntry;
    }

}
