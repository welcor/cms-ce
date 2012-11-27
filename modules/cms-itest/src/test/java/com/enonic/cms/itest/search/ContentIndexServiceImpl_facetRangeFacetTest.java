package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.result.FacetResultSet;
import com.enonic.cms.core.search.result.FacetsResultSet;
import com.enonic.cms.core.search.result.RangeFacetResultEntry;
import com.enonic.cms.core.search.result.RangeFacetResultSet;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_facetRangeFacetTest
    extends ContentIndexServiceFacetTestBase
{

    @Test
    public void dates()
    {
        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range to=\"2001-01-01\"/>\n" +
            "            <range from=\"2001-01-01\" to=\"2001-01-02\"/>\n" +
            "            <range from=\"2001-01-01T00:00:01:001\" to=\"2001-01-01 23:59:59\"/>\n" +
            "            <range from=\"2001-01-02T\" />\n" +
            "        <index>data.myDate</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        query.setFacets( facetDefinition );

        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        createContent( 1, "2000-12-31 23:59:59", "data/myDate" );
        createContent( 2, "2001-01-01 00:00:00", "data/myDate" );
        createContent( 3, "2001-01-01 01:00:00", "data/myDate" );
        createContent( 4, "2001-01-01 01:00:00:001", "data/myDate" );
        createContent( 5, "2001-01-01 23:59:58", "data/myDate" );
        createContent( 6, "2001-01-02", "data/myDate" );
        createContent( 7, "2001-01-03", "data/myDate" );

        flushIndex();

        final ContentResultSet result = contentIndexService.query( query );

        final Iterator<RangeFacetResultEntry> iterator = getResultIterator( result, 4 );

        // NOTE: ES-range facets threats 'from' as 'from & including' and 'to' as 'to !including'
        assertNextEntry( iterator, 1L ); // 2000-12-31 23:59:59
        assertNextEntry( iterator, 4L ); // 2001-01-01 00:00:00, 2001-01-01 01:00:00, 2001-01-01 01:00:00:001, 2001-01-01 23:59:58
        assertNextEntry( iterator, 3L ); // 2001-01-01 01:00:00, 2001-01-01 01:00:00:001, 2001-01-01 23:59:58
        assertNextEntry( iterator, 2L ); // 2001-01-02, 2001-01-03

        final String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "      <range hits=\"1\" to=\"2001-01-01 00:00:00\" />\n" +
            "      <range hits=\"4\" from=\"2001-01-01 00:00:00\" to=\"2001-01-02 00:00:00\" />\n" +
            "      <range hits=\"3\" from=\"2001-01-01 00:00:01\" to=\"2001-01-01 23:59:59\" />\n" +
            "      <range hits=\"2\" from=\"2001-01-02 00:00:00\" />\n" +
            "    </ranges>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void numeric()
    {
        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range to=\"1\"/>\n" +
            "            <range from=\"1\" to=\"10\"/>\n" +
            "            <range from=\"10\" to=\"100\"/>\n" +
            "            <range from=\"100\" />\n" +
            "        <index>data.price</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        query.setFacets( facetDefinition );

        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        createContent( 1, "0", "data/price" );
        createContent( 2, "0.99", "data/price" );
        createContent( 3, "1", "data/price" );
        createContent( 4, "1.0", "data/price" );
        createContent( 5, "10.0", "data/price" );
        createContent( 6, "100", "data/price" );
        createContent( 7, "101", "data/price" );
        createContent( 8, "1000", "data/price" );

        flushIndex();

        final ContentResultSet result = contentIndexService.query( query );

        final Iterator<RangeFacetResultEntry> iterator = getResultIterator( result, 4 );

        // NOTE: ES-range facets threats 'from' as 'from & including' and 'to' as 'to !including'
        assertNextEntry( iterator, 2L ); // 0, 0.99
        assertNextEntry( iterator, 2L ); // 1, 1.0
        assertNextEntry( iterator, 1L ); // 10.0
        assertNextEntry( iterator, 3L ); // 100, 101, 1000

        final String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "      <range hits=\"2\" to=\"1.0\" min=\"0.0\" mean=\"0.495\" max=\"0.99\" sum=\"0.99\" />\n" +
            "      <range hits=\"2\" from=\"1.0\" to=\"10.0\" min=\"1.0\" mean=\"1.0\" max=\"1.0\" sum=\"2.0\" />\n" +
            "      <range hits=\"1\" from=\"10.0\" to=\"100.0\" min=\"10.0\" mean=\"10.0\" max=\"10.0\" sum=\"10.0\" />\n" +
            "      <range hits=\"3\" from=\"100.0\" min=\"100.0\" mean=\"400.3333333333333\" max=\"1000.0\" sum=\"1201.0\" />\n" +
            "    </ranges>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( result, expectedXml );

    }


    @Test
    public void numeric_key_and_valuefields()
    {
        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "            <range to=\"1\"/>\n" +
            "            <range from=\"1\" to=\"10\"/>\n" +
            "            <range from=\"10\" to=\"100\"/>\n" +
            "            <range from=\"100\" />\n" +
            "        <index>data.price</index>\n" +
            "        <value-index>data.myValue</value-index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        query.setFacets( facetDefinition );

        createAndIndexContent( 1, new String[]{"0", "2"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 2, new String[]{"0.99", "4"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 3, new String[]{"1", "6"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 4, new String[]{"1.0", "8"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 5, new String[]{"10.0", "10"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 6, new String[]{"100", "12"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 7, new String[]{"101", "14"}, new String[]{"data.price", "data.myValue"} );
        createAndIndexContent( 8, new String[]{"1000", "16"}, new String[]{"data.price", "data.myValue"} );

        flushIndex();

        final ContentResultSet result = contentIndexService.query( query );

        final Iterator<RangeFacetResultEntry> iterator = getResultIterator( result, 4 );

        // NOTE: ES-range facets threats 'from' as 'from & including' and 'to' as 'to !including'
        assertNextEntry( iterator, 2L ); // 0, 0.99
        assertNextEntry( iterator, 2L ); // 1, 1.0
        assertNextEntry( iterator, 1L ); // 10.0
        assertNextEntry( iterator, 3L ); // 100, 101, 1000

        final String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <ranges name=\"myRangeFacet\">\n" +
            "      <range hits=\"2\" to=\"1.0\" min=\"2.0\" mean=\"3.0\" max=\"4.0\" sum=\"6.0\" />\n" +
            "      <range hits=\"2\" from=\"1.0\" to=\"10.0\" min=\"6.0\" mean=\"7.0\" max=\"8.0\" sum=\"14.0\" />\n" +
            "      <range hits=\"1\" from=\"10.0\" to=\"100.0\" min=\"10.0\" mean=\"10.0\" max=\"10.0\" sum=\"10.0\" />\n" +
            "      <range hits=\"3\" from=\"100.0\" min=\"12.0\" mean=\"14.0\" max=\"16.0\" sum=\"42.0\" />\n" +
            "    </ranges>\n" +
            "  </facets>\n" +
            "</content>\n";

        createAndCompareResultAsXml( result, expectedXml );
    }

    private Iterator<RangeFacetResultEntry> getResultIterator( final ContentResultSet result, int expectedHits )
    {
        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertTrue( facetsResultSet.iterator().hasNext() );
        final FacetResultSet next = facetsResultSet.iterator().next();
        assertTrue( next instanceof RangeFacetResultSet );
        RangeFacetResultSet rangeFacetResultSet = (RangeFacetResultSet) next;
        assertEquals( "Wrong number of ranges", expectedHits, rangeFacetResultSet.getResultEntries().size() );

        return rangeFacetResultSet.getResultEntries().iterator();
    }

    private void assertNextEntry( final Iterator<RangeFacetResultEntry> iterator, final Long count )
    {
        final RangeFacetResultEntry currentEntry = iterator.next();
        assertEquals( count, currentEntry.getCount() );
    }

    private ContentDocument createContent( int contentKey, final String dateString, final String fieldName )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( fieldName, dateString );
        contentIndexService.index( doc1 );
        return doc1;
    }

}
