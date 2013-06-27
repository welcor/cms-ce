/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.result.FacetResultSetXmlCreator;

import static org.junit.Assert.*;

public class ContentIndexServiceFacetTestBase
    extends ContentIndexServiceTestBase
{
    private final FacetResultSetXmlCreator facetResultSetXmlCreator = new FacetResultSetXmlCreator();

    protected void createAndCompareResultAsXml( final ContentResultSet result, final String expectedXml )
    {
        Document doc = new Document();
        doc.addContent( new Element( "content" ) );

        facetResultSetXmlCreator.addFacetResultXml( doc, result.getFacetsResultSet() );
        final String resultXml = JDOMUtil.prettyPrintDocument( doc );

        final String expectedXmlTrimmed = expectedXml.replace( "\n", "" ).replace( "\r", "" );
        final String resultXmlTrimmed = resultXml.replace( "\n", "" ).replace( "\r", "" );

        // Trickery to get the nice output of diff
        if ( !expectedXmlTrimmed.equals( resultXmlTrimmed ) )
        {
            assertEquals( expectedXml, resultXml );
        }
        else
        {
            assertEquals( expectedXmlTrimmed, resultXmlTrimmed );
        }
    }

    protected ContentDocument createAndIndexContent( int contentKey, final String customDataValue, final String fieldName )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( fieldName, customDataValue );
        contentIndexService.index( doc1 );
        return doc1;
    }

    protected ContentDocument createAndIndexContent( int contentKey, String[] values, String[] fields )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );

        for ( int i = 0; i < fields.length; i++ )
        {
            doc1.addUserDefinedField( fields[i], values[i] );
        }

        contentIndexService.index( doc1 );
        return doc1;
    }

    protected void setMetadata( final GregorianCalendar date, final ContentDocument doc1 )
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
