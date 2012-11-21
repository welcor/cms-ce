package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
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
            "            <range from=\"2001-01-01 00:00:01:001\" to=\"2001-01-01 23:59:59\"/>\n" +
            "            <range from=\"2001-01-02\" />\n" +
            "        <index>data.myDate</index>\n" +
            "    </ranges>\n" +
            "</facets>\n";

        query.setFacets( facetDefinition );

        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        createDocumentWithDate( date, 1, "2000-12-31 23:59:59", "data/myDate" );
        createDocumentWithDate( date, 2, "2001-01-01 00:00:00", "data/myDate" );
        createDocumentWithDate( date, 3, "2001-01-01 01:00:00", "data/myDate" );
        createDocumentWithDate( date, 4, "2001-01-01 01:00:00:001", "data/myDate" );
        createDocumentWithDate( date, 5, "2001-01-01 23:59:58", "data/myDate" );
        createDocumentWithDate( date, 6, "2001-01-02", "data/myDate" );
        createDocumentWithDate( date, 7, "2001-01-03", "data/myDate" );

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
            "      <result count=\"1\" to=\"2001-01-01 00:00:00\" />\n" +
            "      <result count=\"4\" from=\"2001-01-01 00:00:00\" to=\"2001-01-02 00:00:00\" />\n" +
            "      <result count=\"3\" from=\"2001-01-01 00:00:01\" to=\"2001-01-01 23:59:59\" />\n" +
            "      <result count=\"2\" from=\"2001-01-02 00:00:00\" />\n" +
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

        createDocumentWithDate( date, 1, "0", "data/price" );
        createDocumentWithDate( date, 2, "0.99", "data/price" );
        createDocumentWithDate( date, 3, "1", "data/price" );
        createDocumentWithDate( date, 4, "1.0", "data/price" );
        createDocumentWithDate( date, 5, "10.0", "data/price" );
        createDocumentWithDate( date, 6, "100", "data/price" );
        createDocumentWithDate( date, 7, "101", "data/price" );
        createDocumentWithDate( date, 8, "1000", "data/price" );

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
            "      <result count=\"2\" to=\"1.0\" min=\"0.0\" mean=\"0.495\" max=\"0.99\" />\n" +
            "      <result count=\"2\" from=\"1.0\" to=\"10.0\" min=\"1.0\" mean=\"1.0\" max=\"1.0\" />\n" +
            "      <result count=\"1\" from=\"10.0\" to=\"100.0\" min=\"10.0\" mean=\"10.0\" max=\"10.0\" />\n" +
            "      <result count=\"3\" from=\"100.0\" min=\"100.0\" mean=\"400.3333333333333\" max=\"1000.0\" />\n" +
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

    private ContentDocument createDocumentWithDate( final GregorianCalendar date, int contentKey, final String dateString,
                                                    final String fieldName )
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( fieldName, dateString );
        contentIndexService.index( doc1 );
        return doc1;
    }

    private void setMetadata( final GregorianCalendar date, final ContentDocument doc1 )
    {
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Species" );
        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        doc1.setLanguageCode( "en" );
    }

}
