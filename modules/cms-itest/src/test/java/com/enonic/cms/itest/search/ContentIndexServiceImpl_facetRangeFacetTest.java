package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
    public void single_facet()
    {
        setUpValuesWithFacetGoodies();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetDefinition = "<facets>\n" +
            "    <range>\n" +
            "        <name>myRangeFacet</name>\n" +
            "        <ranges>\n" +
            "            <range>\n" +
            "                <to>2000-12-31 23:59:59:999</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>2001-01-01 00:00</from>\n" +
            "                <to>2001-01-01 23:59</to>\n" +
            "            </range>\n" +
            "            <range>\n" +
            "                <from>2001-01-02</from>\n" +
            "            </range>\n" +
            "        </ranges>\n" +
            "        <field>data/myDate</field>\n" +
            "    </range>\n" +
            "</facets>\n";
        System.out.println( facetDefinition );

        printAllIndexContent();

        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertTrue( facetsResultSet.iterator().hasNext() );
        final FacetResultSet next = facetsResultSet.iterator().next();
        assertTrue( next instanceof RangeFacetResultSet );
        RangeFacetResultSet rangeFacetResultSet = (RangeFacetResultSet) next;
        assertEquals( 3, rangeFacetResultSet.getResultEntries().size() );

        for ( RangeFacetResultEntry resultEntry : rangeFacetResultSet.getResultEntries() )
        {
            System.out.println( resultEntry.getFrom() + " - " + resultEntry.getTo() + " : " + resultEntry.getCount() );
        }

        System.out.println( result.toString() );
    }


    protected void setUpValuesWithFacetGoodies()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        createDocumentWithDate( date, 2, "2000-12-31 23:59" );
        createDocumentWithDate( date, 3, "2001-01-01 01:00:00" );
        createDocumentWithDate( date, 4, "2001-01-01 01:00:00:001" );
        createDocumentWithDate( date, 5, "2001-01-01 23:59:59" );
        createDocumentWithDate( date, 6, "2001-01-02" );
        createDocumentWithDate( date, 7, "2001-01-03" );
        /*
        createDocumentWithDate( date, 7, "2001-01-04" );
        createDocumentWithDate( date, 8, "2001-01-05" );
        createDocumentWithDate( date, 9, "2001-01-06" );
        createDocumentWithDate( date, 10, "2001-01-01" );
        createDocumentWithDate( date, 11, "2001-01-01" );
        createDocumentWithDate( date, 12, "2001-01-01" );
        createDocumentWithDate( date, 13, "2001-01-01" );
        createDocumentWithDate( date, 14, "2001-01-01" );
        createDocumentWithDate( date, 15, "2001-01-01" );
        createDocumentWithDate( date, 16, "2001-01-01" );
        createDocumentWithDate( date, 17, "2001-01-01" );
        */

        flushIndex();
    }

    private ContentDocument createDocumentWithDate( final GregorianCalendar date, int contentKey, final String dateString )
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( "data/myDate", dateString );
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
