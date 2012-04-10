package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.elasticsearch.search.SearchHitField;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ContentIndexServiceImplTest_store_datatypes
    extends ContentIndexServiceTestBase
{

    @Test
    public void testQueryOnNumericAndNotNumericValue()
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
        contentIndexService.index( doc1, true );

        final ContentKey contentKey = new ContentKey( 1322 );
        final Map<String, SearchHitField> fieldMapForId = getFieldMapForId( contentKey );

        final SearchHitField data_person_age = fieldMapForId.get( "data_person_age" );
        assertNotNull( data_person_age );
        final SearchHitField data_person_age_numeric = fieldMapForId.get( "data_person_age_numeric" );
        assertNotNull( data_person_age_numeric );

        final Object age = data_person_age.getValue();
        ContentIndexQuery query = new ContentIndexQuery( "data/person/age = " + age + " AND " +
                                                             "data/person/age = '" + age + "'" );

        final ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 1, resultSet.getTotalCount() );
        assertTrue( resultSet.containsContent( contentKey ) );
    }


}
