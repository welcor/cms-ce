/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.query.ContentDocument;

import static junit.framework.Assert.assertNotNull;

public class ContentIndexServiceImpl_indexAllUserdataFieldTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testBuildsCorrectAllUserDataFields()
    {
        indexUserDataWithMultipleValuesDocument();

        final Map<String, Object> fieldMapForId = getFieldMapForId( new ContentKey( 1322 ) );

        final Object all_userdata = fieldMapForId.get( "_all_userdata" );
        assertNotNull( all_userdata );

        verifyField( "_all_userdata", 4, fieldMapForId );
    }

    @Test
    public void testDataStarQueryWithNumericStringValue()
    {
        indexUserDataWithMultipleValuesDocument();

        assertContentResultSetEquals( new int[]{1322}, contentIndexService.query( new ContentIndexQuery( "data/* = '11' " ) ) );
    }

    @Test
    public void testDataStarQueryWithNumericValue()
    {
        indexUserDataWithMultipleValuesDocument();

        printAllIndexContent();

        assertContentResultSetEquals( new int[]{1322}, contentIndexService.query( new ContentIndexQuery( "data/* = 10 " ) ) );

        assertContentResultSetEquals( new int[]{1322}, contentIndexService.query( new ContentIndexQuery( "data/* = 11 " ) ) );

    }

    private void indexUserDataWithMultipleValuesDocument()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc1 = new ContentDocument( new ContentKey( 1322 ) );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( "data/test/number", "10" );
        doc1.addUserDefinedField( "data/test/number", "11" );
        doc1.addUserDefinedField( "data/test/number2", "10" );
        doc1.addUserDefinedField( "data/test/number2", "11" );
        doc1.addUserDefinedField( "data/test/value", "ten" );
        doc1.addUserDefinedField( "data/test/value", "eleven" );
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1 );

        ContentDocument doc2 = new ContentDocument( new ContentKey( 1323 ) );
        doc2.setCategoryKey( new CategoryKey( 9 ) );
        doc2.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc2.setContentTypeName( "Adults" );
        doc2.setTitle( "Homer" );
        doc1.addUserDefinedField( "data/test/number", "11" );
        doc1.addUserDefinedField( "data/test/number", "12" );
        doc1.addUserDefinedField( "data/test/number2", "11" );
        doc1.addUserDefinedField( "data/test/number2", "12" );
        doc1.addUserDefinedField( "data/test/value", "eleven" );
        doc1.addUserDefinedField( "data/test/value", "twelve" );
        doc2.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc2.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc2.setStatus( 2 );
        doc2.setPriority( 0 );
        contentIndexService.index( doc2 );

        flushIndex();

    }


}
