package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexFieldSet;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/13/12
 * Time: 2:05 PM
 */
public class ContentIndexServiceImpl_queryLargeTextTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testLargeTextFieldShortenValue()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );

        int numberOfRowsExpected = 5;
        doc1.addUserDefinedField( "data/text", createStringFillingXRows( numberOfRowsExpected ) );

        contentIndexService.index( doc1, false );

        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );

        // Set new, shorter value for the text-field, and index again
        doc1 = createContentDocWithNoUserFields( contentKey );

        int newNumberOfRowsExpected = 3;
        doc1.addUserDefinedField( "data/text", createStringFillingXRows( newNumberOfRowsExpected ) );

        contentIndexService.index( doc1, false );
        //flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
    }

    @Test
    public void testLargeTextFieldExtendValue()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );

        int numberOfRowsExpected = 5;
        doc1.addUserDefinedField( "data/text", createStringFillingXRows( numberOfRowsExpected ) );

        contentIndexService.index( doc1, false );

        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );

        // Set new, shorter value for the text-field, and index again
        doc1 = createContentDocWithNoUserFields( contentKey );

        int newNumberOfRowsExpected = 10;
        doc1.addUserDefinedField( "data/text", createStringFillingXRows( newNumberOfRowsExpected ) );

        contentIndexService.index( doc1, false );
        //flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
    }

    @Test
    public void testLargeTextFieldRemoveValue()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );

        int numberOfRowsExpected = 5;
        doc1.addUserDefinedField( "data/text", createStringFillingXRows( numberOfRowsExpected ) );

        contentIndexService.index( doc1, false );
        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );

        // Set new, shorter value for the text-field, and index again
        doc1 = createContentDocWithNoUserFields( contentKey );

        contentIndexService.index( doc1, false );
        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
    }

    @Test
    public void testLargeTextFieldChangeStatus()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );
        doc1.setStatus( 1 );

        int numberOfRowsExpected = 5;
        String userText = createStringFillingXRows( numberOfRowsExpected );
        doc1.addUserDefinedField( "data/text", userText );

        contentIndexService.index( doc1, false );
        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );

        // Set new status, this should be populated to all index-values
        doc1 = createContentDocWithNoUserFields( contentKey );
        doc1.setStatus( 2 );

        int newNumberOfRowsExpected = 5;
        doc1.addUserDefinedField( "data/text", userText );

        contentIndexService.index( doc1, false );
        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
    }

    @Test
    public void testLargeBinaryExtractedTextFieldShortenValue()
    {
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );
        ContentDocument doc1 = createContentDocWithNoUserFields( contentKey );

        int numberOfRowsExpected = 10;
        doc1.setBinaryExtractedText( new BigText( createStringFillingXRows( numberOfRowsExpected ) ) );

        contentIndexService.index( doc1, false );
        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
        // Set new, shorter value for the text-field, and index again
        doc1 = createContentDocWithNoUserFields( contentKey );

        int newNumberOfRowsExpected = 3;
        doc1.setBinaryExtractedText( new BigText( createStringFillingXRows( newNumberOfRowsExpected ) ) );

        contentIndexService.index( doc1, false );
        flushIndex();
        verifyStandardFields( doc1, contentKey );
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

    private String createStringFillingXRows( int numberOfRows )
    {
        return createRandomTextOfSize( ContentIndexFieldSet.SPLIT_TRESHOLD * numberOfRows - 5 );
    }

    private String createRandomTextOfSize( int size )
    {
        String str = new String( "ABCDEFGHIJKLMNOPQRSTUVWZYZabcdefghijklmnopqrstuvw " );
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int te = 0;
        for ( int i = 1; i <= size; i++ )
        {
            te = r.nextInt( str.length() - 1 );
            sb.append( str.charAt( te ) );
        }

        return sb.toString();
    }

}
