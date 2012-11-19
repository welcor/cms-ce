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
    extends ContentIndexServiceTestBase
{
    @Test
    public void dates()
    {
        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetDefinition = "<facets>\n" +
            "    <range name=\"myRangeFacet\">\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "                <to>2001-01-01</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>2001-01-01</from>\n" +
            "                <to>2001-01-02</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>2001-01-01 00:00:01:001</from>\n" +
            "                <to>2001-01-01 23:59:59</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>2001-01-02</from>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>data/myDate</field>\n" +
            "    </range>\n" +
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

        System.out.println( result.toString() );
    }

    @Test
    public void numeric()
    {
        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetDefinition = "<facets>\n" +
            "    <range name=\"myRangeFacet\">\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "                <to>1</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>1</from>\n" +
            "                <to>10</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>10</from>\n" +
            "                <to>100</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>100</from>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>data/price</field>\n" +
            "    </range>\n" +
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

        System.out.println( result.toString() );
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
