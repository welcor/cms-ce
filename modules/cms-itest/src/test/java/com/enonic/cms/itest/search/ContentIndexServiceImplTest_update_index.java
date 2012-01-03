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
        service.index( doc1, false );

        letTheIndexFinishItsWork();

        ContentIndexQuery query = new ContentIndexQuery( "key = 1322" );
        query.setCount( 10 );

        ContentResultSet resultSet = service.query( query );
        assertEquals( 1, resultSet.getTotalCount() );

        verifyStandardValues( doc1, resultSet );

        //checkUserDefinedFields( indexes, doc1.getUserDefinedFields(), doc1.getUserDefinedFields().size(),
        //                        doc1.getUserDefinedFields().size() );
    }


    @Test
    public void change_status()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
        doc1.setStatus( 1 );
        service.index( doc1, false );
        letTheIndexFinishItsWork();

        ContentIndexQuery query = new ContentIndexQuery( "key = 1322" );
        query.setCount( 10 );
        ContentResultSet resultSet = service.query( query );
        assertEquals( 1, resultSet.getTotalCount() );
        verifyStandardValues( doc1, resultSet );

        // Update status
        doc1 = createContentDocWithNoUserFields( contentKey );
        doc1.setStatus( 2 );
        service.index( doc1, false );
        letTheIndexFinishItsWork();

        resultSet = service.query( query );
        assertEquals( 1, resultSet.getTotalCount() );
        verifyStandardValues( doc1, resultSet );
    }


    private void setUpStandardTestValues()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        // Index content 1, 2 og 3:
        ContentDocument doc1 = new ContentDocument( new ContentKey( 1322 ) );
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
        service.index( doc1, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc2 = new ContentDocument( new ContentKey( 1327 ) );
        doc2.setCategoryKey( new CategoryKey( 7 ) );
        doc2.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc2.setContentTypeName( "Adults" );
        doc2.setTitle( "Fry" );
        doc2.addUserDefinedField( "data/person/age", "28" );
        doc2.addUserDefinedField( "data/person/gender", "male" );
        doc2.addUserDefinedField( "data/person/description", "an extratemporal character, unable to comprehend the future" );
        // Publish from February 29th to March 29th.
        doc2.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc2.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc2.setStatus( 2 );
        doc2.setPriority( 0 );
        service.index( doc2, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc3 = new ContentDocument( new ContentKey( 1323 ) );
        doc3.setCategoryKey( new CategoryKey( 9 ) );
        doc3.setContentTypeKey( new ContentTypeKey( 37 ) );
        doc3.setContentTypeName( "Children" );
        doc3.setTitle( "Bart" );
        doc3.addUserDefinedField( "data/person/age", "10" );
        doc3.addUserDefinedField( "data/person/gender", "male" );
        doc3.addUserDefinedField( "data/person/description", "mischievous, rebellious, disrespecting authority and sharp witted" );
        // Publish from March 1st to April 1st
        doc3.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc3.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc3.setStatus( 2 );
        doc3.setPriority( 0 );
        service.index( doc3, true );

        ContentDocument doc4 = new ContentDocument( new ContentKey( 1324 ) );
        doc4.setCategoryKey( new CategoryKey( 9 ) );
        doc4.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc4.setContentTypeName( "Adults" );
        doc4.setTitle( "Bender" );
        doc4.addUserDefinedField( "data/person/age", "5" );
        doc4.addUserDefinedField( "data/person/gender", "man-bot" );
        doc4.addUserDefinedField( "data/person/description",
                                  "alcoholic, whore-mongering, chain-smoking gambler with a swarthy Latin charm" );
        // Publish from March 1st to March 28th.
        doc4.setPublishFrom( date.getTime() );
        date.add( Calendar.DAY_OF_MONTH, 27 );
        doc4.setPublishTo( date.getTime() );
        doc4.setStatus( 2 );
        doc4.setPriority( 0 );
        service.index( doc4, true );

        letTheIndexFinishItsWork();
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
