package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.result.FacetResultSet;
import com.enonic.cms.core.search.result.HistogramFacetResultSet;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_facetHistogramFacetTest
    extends ContentIndexServiceFacetTestBase
{

    @Test
    public void simple_histogram()
    {
        createAndIndexContent( 1, "100", "data.myValue" );
        createAndIndexContent( 2, "101", "data.myValue" );
        createAndIndexContent( 3, "200", "data.myValue" );
        createAndIndexContent( 4, "201", "data.myValue" );
        createAndIndexContent( 5, "300", "data.myValue" );
        createAndIndexContent( 6, "401", "data.myValue" );
        createAndIndexContent( 7, "501", "data.myValue" );
        createAndIndexContent( 8, "1000", "data.myValue" );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetDefinition = "<facets>\n" +
            "    <histogram name=\"myHistogramFacet\">\n" +
            "        <index>data.myValue</index>\n" +
            "        <interval>100</interval>\n" +
            "    </histogram>\n" +
            "</facets>\n";

        query.setFacets( facetDefinition );
        final ContentResultSet result = contentIndexService.query( query );

        final FacetResultSet next = result.getFacetsResultSet().iterator().next();
        assertNotNull( next );
        assertTrue( next instanceof HistogramFacetResultSet );
        HistogramFacetResultSet histogramFacetResultSet = (HistogramFacetResultSet) next;
        assertEquals( 6, histogramFacetResultSet.getResultEntries().size() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <histogram name=\"myHistogramFacet\">\n" +
            "      <result count=\"2\">100</result>\n" +
            "      <result count=\"2\">200</result>\n" +
            "      <result count=\"1\">300</result>\n" +
            "      <result count=\"1\">400</result>\n" +
            "      <result count=\"1\">500</result>\n" +
            "      <result count=\"1\">1000</result>\n" +
            "    </histogram>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void key_and_value_fields()
    {
        createAndIndexContent( 1, new String[]{"100", "1"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 2, new String[]{"101", "2"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 3, new String[]{"200", "3"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 4, new String[]{"201", "4"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 5, new String[]{"300", "5"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 6, new String[]{"401", "6"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 7, new String[]{"501", "7"}, new String[]{"data.myKey", "data.myValue"} );
        createAndIndexContent( 8, new String[]{"1000", "8"}, new String[]{"data.myKey", "data.myValue"} );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetDefinition = "<facets>\n" +
            "    <histogram name=\"myHistogramFacet\">\n" +
            "        <key-field>data.myKey</key-field>\n" +
            "        <value-field>data.myValue</value-field>\n" +
            "        <interval>100</interval>\n" +
            "    </histogram>\n" +
            "</facets>\n";

        query.setFacets( facetDefinition );
        final ContentResultSet result = contentIndexService.query( query );

        final FacetResultSet next = result.getFacetsResultSet().iterator().next();
        assertNotNull( next );
        assertTrue( next instanceof HistogramFacetResultSet );
        HistogramFacetResultSet histogramFacetResultSet = (HistogramFacetResultSet) next;
        assertEquals( 6, histogramFacetResultSet.getResultEntries().size() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <histogram name=\"myHistogramFacet\">\n" +
            "      <result total=\"3.0\" count=\"2\" min=\"1.0\" mean=\"1.5\" max=\"2.0\">100</result>\n" +
            "      <result total=\"7.0\" count=\"2\" min=\"3.0\" mean=\"3.5\" max=\"4.0\">200</result>\n" +
            "      <result total=\"5.0\" count=\"1\" min=\"5.0\" mean=\"5.0\" max=\"5.0\">300</result>\n" +
            "      <result total=\"6.0\" count=\"1\" min=\"6.0\" mean=\"6.0\" max=\"6.0\">400</result>\n" +
            "      <result total=\"7.0\" count=\"1\" min=\"7.0\" mean=\"7.0\" max=\"7.0\">500</result>\n" +
            "      <result total=\"8.0\" count=\"1\" min=\"8.0\" mean=\"8.0\" max=\"8.0\">1000</result>\n" +
            "    </histogram>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( result, expectedXml );
    }


    private ContentDocument createAndIndexContent( int contentKey, final String customDataValue, final String fieldName )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( fieldName, customDataValue );
        contentIndexService.index( doc1 );
        return doc1;
    }

}
