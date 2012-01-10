package com.enonic.cms.core.search.builder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import junit.framework.Assert;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexFieldSet;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 11:54 AM
 */
public class ContentIndexDataBuilderImplTest
{

    protected final static String[] REQUIRED_ORDERBY_FIELDS =
        new String[]{"orderby_categorykey", "orderby_contenttype", "orderby_contenttypekey", "orderby_key", "orderby_priority",
            "orderby_publishfrom", "orderby_status", "orderby_title"};

    protected final static String[] REQUIRED_STANDARD_FIELD =
        new String[]{"categorykey", "categorykey_numeric", "contenttype", "contenttypekey", "contenttypekey_numeric", "key", "key_numeric",
            "priority", "priority_numeric", "publishfrom", "status", "status_numeric", "title"};

    ContentIndexDataBuilder indexDataBuilder = new ContentIndexDataBuilderImpl();

    @Test
    public void testMetadata()
        throws Exception
    {
        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = indexDataBuilder.build( content, spec );

        /*ContentBuilderTestMetaDataHolder metadata = ContentBuilderTestMetaDataHolder.createMetaDataHolder( indexData.getMetadataJson() );

        assertEquals( 1.0, metadata.key );
        assertEquals( "mytitle", metadata.title );
        assertEquals( new Integer( 2 ), metadata.status );
        assertEquals( "2011-02-09T23:00:00.000Z", metadata.publishto );
        assertEquals( "2011-01-09T23:00:00.000Z", metadata.publishfrom );
        assertEquals( "2011-03-09T23:00:00.000Z", metadata.timestamp );
        assertEquals( "mycontenttype", metadata.contenttype );
        assertEquals( (int) 3, (int) metadata.contenttype_key );

        assertEquals( 12.0, metadata.assignee_key );
        assertEquals( "assigneeqname", metadata.assignee_qualifiedname );

        assertEquals( 14.0, metadata.assigner_key );
        assertEquals( "assignerqname", metadata.assigner_qualifiedname );

        assertEquals( "2011-03-09T23:00:00.000Z", metadata.assignmentduedate );

        assertEquals( 10.0, metadata.modifier_key );
        assertEquals( "modifierqname", metadata.modifier_qualifiedname );

        assertEquals( 11.0, metadata.owner_key );
        assertEquals( "ownerqname", metadata.owner_qualifiedname );

        assertEquals( new Integer( 1 ), metadata.priority );

        assertEquals( 2.0, metadata.category_key );
        assertEquals( "mycategory", metadata.category_name );
        */

        final String indexDataAsString = indexData.getMetadataJson();

        for ( String field : REQUIRED_STANDARD_FIELD )
        {
            assertTrue( "Missing required field: " + field, indexDataAsString.contains( field ) );
        }

        for ( String field : REQUIRED_ORDERBY_FIELDS )
        {
            assertTrue( "Missing required orderby field: " + field, indexDataAsString.contains( field ) );
        }
    }

    @Test
    public void testOrderByFields()
        throws Exception
    {
        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = indexDataBuilder.build( content, spec );

        final String indexDataAsString = indexData.getMetadataJson();

        System.out.println( indexDataAsString );

        for ( String field : REQUIRED_ORDERBY_FIELDS )
        {
            assertTrue( "Missing required orderby field: " + field, indexDataAsString.contains( field ) );
        }

        final List<String> keysAsList = getKeysAsList( indexDataAsString );

        for ( String key : keysAsList )
        {
            if ( StringUtils.startsWith( key, IndexFieldNameConstants.ORDER_FIELD_PREFIX ) )
            {
                assertEquals( "Only one instance of each key allowed: " + key, 1, Collections.frequency( keysAsList, key ) );
            }
        }

        // Check orderby for customdata fields
        Assert.assertTrue( keysAsList.contains( IndexFieldNameConstants.ORDER_FIELD_PREFIX + "data_person_age" ) );
        Assert.assertTrue( keysAsList.contains( IndexFieldNameConstants.ORDER_FIELD_PREFIX + "data_person_description" ) );
        Assert.assertTrue( keysAsList.contains( IndexFieldNameConstants.ORDER_FIELD_PREFIX + "data_person_gender" ) );
    }

    private Map<String, String> getIndexDataAsMap( String indexData )
    {
        Map<String, String> indexDataAsMap = new HashMap<String, String>();

        final String[] split = StringUtils.split( indexData, "," );

        for ( int i = 0; i < split.length; i++ )
        {
            final String keyValue = split[i];

            final String cleaned = StringUtils.remove( keyValue, '"' );

            final String[] keyValueArray = StringUtils.split( cleaned, ":" );

            indexDataAsMap.put( keyValueArray[0], keyValueArray[1] );
        }

        return indexDataAsMap;
    }

    private List<String> getKeysAsList( String indexData )
    {

        final String[] split = StringUtils.split( indexData, "," );
        List<String> keys = new ArrayList<String>();

        for ( int i = 0; i < split.length; i++ )
        {
            final String keyValue = split[i];

            final String cleaned = StringUtils.remove( keyValue, '"' );

            final String[] keyValueArray = StringUtils.split( cleaned, ":" );

            keys.add( keyValueArray[0] );
        }

        return keys;
    }

    /*
    @Test
    public void testCustomData()
        throws Exception
    {
        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createBuildAllConfig();

        ContentIndexData indexData = indexDataBuilder.build( content, spec );

        ContentBuilderTestCustomDataHolder customdata =
            ContentBuilderTestCustomDataHolder.createCustomDataHolder( indexData.getCustomdataJson() );

        assertNotNull( indexData.getCustomdataJson() );
        assertEquals( 1.0, customdata.key );
    }
    */

    private ContentDocument createTestContent()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2011, Calendar.JANUARY, 10 );

        ContentDocument content = new ContentDocument( new ContentKey( 1 ) );
        content.setCategoryKey( new CategoryKey( 2 ) );
        content.setCategoryName( "MyCategory" );
        content.setContentTypeKey( new ContentTypeKey( 3 ) );
        content.setContentTypeName( "MyContentType" );

        content.setCreated( date.getTime() );

        content.setModifierKey( "10" );
        content.setModifierName( "ModifierName" );
        content.setModifierQualifiedName( "ModifierQName" );

        content.setOwnerKey( "11" );
        content.setOwnerName( "OwnerName" );
        content.setOwnerQualifiedName( "OwnerQName" );

        content.setAssigneeKey( "12" );
        content.setAssigneeName( "AssigneeName" );
        content.setAssigneeQualifiedName( "AssigneeQName" );

        content.setAssignerKey( "14" );
        content.setAssignerName( "AssignerName" );
        content.setAssignerQualifiedName( "AssignerQName" );

        content.setPublishFrom( date.getTime() );

        date.add( Calendar.MONTH, 1 );
        content.setPublishTo( date.getTime() );

        date.add( Calendar.MONTH, 1 );
        content.setAssignmentDueDate( date.getTime() );

        content.setTimestamp( date.getTime() );

        content.setModified( date.getTime() );

        content.setTitle( "MyTitle" );
        content.setStatus( 2 );
        content.setPriority( 1 );

        // content locations set. but it's really not used now.
        content.setContentLocations( new ContentLocations( new ContentEntity() ) );

        content.addUserDefinedField( "data/person/age", "38" );
        content.addUserDefinedField( "data/person/gender", "male" );
        content.addUserDefinedField( "data/person/description", "description 38" );

        content.addUserDefinedField( "data/person/age", "28" );
        content.addUserDefinedField( "data/person/gender", "male" );
        content.addUserDefinedField( "data/person/description", "description 28" );

        content.addUserDefinedField( "data/person/age", "10" );
        content.addUserDefinedField( "data/person/gender", "male" );
        content.addUserDefinedField( "data/person/description", "description 10" );

        int numberOfRowsExpected = 10;
        content.setBinaryExtractedText( new BigText( createStringFillingXRows( numberOfRowsExpected ) ) );

        return content;
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
