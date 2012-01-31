package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/2/12
 * Time: 3:05 PM
 */
public class ContentIndexServiceImplTest_update_index
    extends ContentIndexServiceTestDBBase
{

    @Test
    public void index_same_document_again()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        // Index the same content again
        ContentDocument doc1 = new ContentDocument( contentKey );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        doc1.addUserDefinedField( "data/person/age", "38" );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/description",
                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, false );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "key = 1322" );
        query.setCount( 10 );

        ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 1, resultSet.getTotalCount() );

        verifyStandardFields( doc1, resultSet.getKey( 0 ) );
        verifyUserDefinedFields( contentKey, doc1 );
    }


    @Test
    public void change_status()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
        doc1.setStatus( 1 );
        contentIndexService.index( doc1, false );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "key = 1322" );
        query.setCount( 10 );
        ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 1, resultSet.getTotalCount() );
        verifyStandardFields( doc1, resultSet.getKey( 0 ) );

        // Update status
        doc1 = createContentDocWithNoUserFields( contentKey );
        doc1.setStatus( 2 );
        contentIndexService.index( doc1, false );
        flushIndex();

        resultSet = contentIndexService.query( query );
        assertEquals( 1, resultSet.getTotalCount() );
        verifyStandardFields( doc1, resultSet.getKey( 0 ) );
        verifyUserDefinedFields( contentKey, doc1 );
    }


    private ContentDocument createContentDocWithNoUserFields( ContentKey contentKey )
    {
        ContentDocument doc1 = new ContentDocument( contentKey );
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        return doc1;
    }
}
