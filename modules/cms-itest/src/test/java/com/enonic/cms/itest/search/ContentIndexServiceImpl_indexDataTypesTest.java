package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.elasticsearch.search.SearchHitField;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.config.IndexFieldType;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class ContentIndexServiceImpl_indexDataTypesTest
    extends ContentIndexServiceTestBase
{

    private ContentDocument createContentDocument( int contentKey )
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setTitle( "Family" );
        doc1.setContentTypeName( "Adults" );
        return doc1;
    }

    private void addMetaData( final ContentDocument doc1 )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );

        doc1.setCreated( date.getTime() );
        doc1.setOwnerName( "testuser" );
        doc1.setOwnerQualifiedName( "enonic/testuser" );
        doc1.setOwnerKey( "1" );

        doc1.setModified( date.getTime() );
        doc1.setModifierKey( "1" );
        doc1.setModifierName( "modifierUser" );
        doc1.setModifierQualifiedName( "enonic/modifierUser" );

        doc1.setAssignmentDueDate( date.getTime() );
        doc1.setAssigneeName( "testuser" );
        doc1.setAssigneeQualifiedName( "enonic/testuser" );
        doc1.setAssigneeKey( "1" );
        doc1.setAssignerName( "testuser" );
        doc1.setAssignerQualifiedName( "enonic/testuser" );
        doc1.setAssignerKey( "1" );
    }

    private void addUserdefinedData( final ContentDocument doc1 )
    {
        doc1.addUserDefinedField( "data/person/age", "36", IndexFieldType.NUMBER );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/description", "description1" );
        doc1.addUserDefinedField( "data/person/birthdate", "1975-05-05", IndexFieldType.DATE );
        doc1.addUserDefinedField( "data/person/samenumber", "1", IndexFieldType.NUMBER );

        doc1.addUserDefinedField( "data/person/age", "18", IndexFieldType.NUMBER );
        doc1.addUserDefinedField( "data/person/gender", "female" );
        doc1.addUserDefinedField( "data/person/description", "description2" );
        doc1.addUserDefinedField( "data/person/birthdate", "1994-06-06", IndexFieldType.DATE );
        doc1.addUserDefinedField( "data/person/samenumber", "1", IndexFieldType.NUMBER );

        doc1.addUserDefinedField( "data/person/age", "37", IndexFieldType.NUMBER );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/description", "description3" );
        doc1.addUserDefinedField( "data/person/birthdate", "1974-06-06", IndexFieldType.DATE );
        doc1.addUserDefinedField( "data/person/samenumber", "1", IndexFieldType.NUMBER );
    }

    @Test
    public void testAllUserFields()
    {
        final int contentKey = 1;
        final ContentDocument contentDocument = createContentDocument( contentKey );
        addUserdefinedData( contentDocument );
        indexContentDocument( contentDocument );
        final Map<String, SearchHitField> fieldMapForId = getAllFieldsForId( contentKey );

        final int numberOfUniqueUserDataValuesInTestValues = 12;

        verifyField( "_all_userdata", numberOfUniqueUserDataValuesInTestValues, fieldMapForId, IndexFieldType.STRING );
        verifyField( "_all_userdata._tokenized", numberOfUniqueUserDataValuesInTestValues, fieldMapForId, IndexFieldType.STRING );
    }

    private Map<String, SearchHitField> getAllFieldsForId( int id )
    {
        final ContentKey contentKey = new ContentKey( id );
        return getFieldMapForId( contentKey );
    }

    @Test
    public void testMetadataFields()
    {
        final int contentKey = 1;

        final ContentDocument contentDocument = createContentDocument( contentKey );
        addMetaData( contentDocument );
        indexContentDocument( contentDocument );
        final Map<String, SearchHitField> fieldMapForId = getAllFieldsForId( contentKey );

        verifyField( "key", 1, fieldMapForId, IndexFieldType.NUMBER );
        verifyField( "title", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "title._tokenized", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "status", 1, fieldMapForId, IndexFieldType.NUMBER );
        verifyField( "publishfrom", 1, fieldMapForId, IndexFieldType.DATE );
        verifyField( "publishto", 1, fieldMapForId, IndexFieldType.DATE );
        verifyField( "contenttype", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "contenttypekey", 1, fieldMapForId, IndexFieldType.NUMBER );

        verifyField( "created", 1, fieldMapForId, IndexFieldType.DATE );
        verifyField( "owner_key", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "owner_name", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "owner_qualifiedname", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "modified", 1, fieldMapForId, IndexFieldType.DATE );
        verifyField( "modifier_key", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "modifier_name", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "modifier_qualifiedname", 1, fieldMapForId, IndexFieldType.STRING );

        verifyField( "assignmentduedate", 1, fieldMapForId, IndexFieldType.DATE );
        verifyField( "assignee_key", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "assignee_name", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "assignee_qualifiedname", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "assigner_key", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "assigner_name", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "assigner_qualifiedname", 1, fieldMapForId, IndexFieldType.STRING );
    }

    private void indexContentDocument( final ContentDocument contentDocument )
    {
        contentIndexService.index( contentDocument, true );
        flushIndex();
    }

    @Test
    public void testStoreUserdefinedFields()
    {
        final int contentKey = 1;
        final ContentDocument contentDocument = createContentDocument( contentKey );
        addUserdefinedData( contentDocument );
        indexContentDocument( contentDocument );
        final Map<String, SearchHitField> fieldMapForId = getAllFieldsForId( contentKey );

        verifyField( "data_person_age", 3, fieldMapForId, IndexFieldType.STRING );
        verifyField( "data_person_age.number", 3, fieldMapForId, IndexFieldType.NUMBER );
        verifyField( "data_person_samenumber", 1, fieldMapForId, IndexFieldType.STRING );
        verifyField( "data_person_samenumber.number", 1, fieldMapForId, IndexFieldType.NUMBER );
        verifyField( "data_person_gender", 2, fieldMapForId, IndexFieldType.STRING );
        verifyField( "data_person_gender.number", 0, fieldMapForId, IndexFieldType.NUMBER );
        verifyField( "data_person_description", 3, fieldMapForId, IndexFieldType.STRING );
        verifyField( "data_person_description.number", 0, fieldMapForId, IndexFieldType.NUMBER );
        verifyField( "data_person_birthdate", 3, fieldMapForId, IndexFieldType.STRING );
        verifyField( "data_person_birthdate.date", 3, fieldMapForId, IndexFieldType.DATE );
    }

    private void verifyField( String fieldName, int expected, Map<String, SearchHitField> fieldMapForId, IndexFieldType indexFieldType )
    {
        final SearchHitField hits = fieldMapForId.get( fieldName );

        if ( expected > 0 )
        {
            assertNotNull( "Hits is null for field: " + fieldName, hits );
            Assert.assertEquals( "Wrong number of hits for field: " + fieldName, expected, hits.values().size() );

            for ( Object hit : hits )
            {
                switch ( indexFieldType )
                {
                    case NUMBER:
                    {
                        assertTrue( hit instanceof Double );
                        break;
                    }
                    case DATE:
                    {
                        try
                        {
                            ISODateTimeFormat.dateTimeParser().parseDateTime( hit.toString() );
                        }
                        catch ( Exception e )
                        {
                            fail( "Not a valid date: " + hit.toString() );
                        }

                        break;
                    }
                    default:
                    {
                        assertTrue( hit instanceof String );
                    }
                }
            }
        }
        else
        {
            assertNull( hits );
        }
    }
}
