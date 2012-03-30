package com.enonic.cms.core.search.builder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import junit.framework.Assert;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexFieldSet;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 11:54 AM
 */
public class ContentIndexDataFactoryTest
{

    protected final static String[] REQUIRED_STANDARD_FIELD =
        new String[]{"categorykey", "contenttype", "contenttypekey", "key", "priority", "publishfrom", "status", "title"};

    ContentIndexDataFactory contentIndexDataFactory = new ContentIndexDataFactory();

    @Test
    public void testMetadata()
        throws Exception
    {
        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = contentIndexDataFactory.create( content, spec );

        final String indexDataAsString = indexData.getMetadataJson();

        for ( String field : REQUIRED_STANDARD_FIELD )
        {
            assertTrue( "Missing required field: " + field, indexDataAsString.contains( field ) );
        }
    }

    @Test
    public void testUserFields()
        throws Exception
    {

        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = contentIndexDataFactory.create( content, spec );

        final String indexDataAsString = indexData.getMetadataJson();

        System.out.println( indexDataAsString );

        final List<String> keysAsList = getKeysAsList( indexDataAsString );

        Assert.assertTrue( keysAsList.contains( "data_person_age" ) );
        Assert.assertTrue( keysAsList.contains( "data_person_description" ) );
        Assert.assertTrue( keysAsList.contains( "data_person_gender" ) );
    }


    @Test
    public void testNumericValues()
        throws Exception
    {

        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = contentIndexDataFactory.create( content, spec );

        final String indexDataAsString = indexData.getMetadataJson();

        System.out.println( indexDataAsString );

        final List<String> keysAsList = getKeysAsList( indexDataAsString );

        verifyFieldExists( keysAsList, "key" );// + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX );
        verifyFieldExists( keysAsList, "status" );// + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX );
        verifyFieldExists( keysAsList, "priority" );// + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX );
        verifyFieldExists( keysAsList, "data_person_age" );// + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX );
    }

    private void verifyFieldExists( List<String> keysAsList, String keyName )
    {
        Assert.assertTrue( "Missing key: " + keyName, keysAsList.contains( keyName ) );
    }

    private void verifyFieldDoesNotExists( List<String> keysAsList, String keyName )
    {
        Assert.assertFalse( "Redundant key: " + keyName, keysAsList.contains( keyName ) );
    }

    @Test
    public void testAttachmentData()
        throws Exception
    {
        ContentDocument content = createTestContent();
        content.setBinaryExtractedText( new BigText( "This is a binary text" ) );

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createBuildAllConfig();

        ContentIndexData indexData = contentIndexDataFactory.create( content, spec );

    }

    @Test
    public void testContentLocations()
        throws Exception
    {

        ContentDocument contentDocument = createTestContent();

        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( "1" ) );

        SiteEntity site = new SiteEntity();
        site.setKey( 1 );
        site.setName( "site1" );

        SectionContentEntity sectionContent1 = createSectionContent( site, content, 1, true );
        SectionContentEntity sectionContent2 = createSectionContent( site, content, 2, true );
        SectionContentEntity sectionContent3 = createSectionContent( site, content, 3, false );

        content.addSectionContent( sectionContent1 );
        content.addSectionContent( sectionContent2 );
        content.addSectionContent( sectionContent3 );

        ContentLocationSpecification spec = new ContentLocationSpecification();
        spec.setSiteKey( new SiteKey( 1 ) );
        spec.setIncludeInactiveLocationsInSection( true );

        contentDocument.setContentLocations( content.getLocations( spec ) );

        assertEquals( 3, contentDocument.getContentLocations().numberOfLocations() );

        ContentIndexDataBuilderSpecification builderSpec = ContentIndexDataBuilderSpecification.createBuildAllConfig();

        ContentIndexData indexData = contentIndexDataFactory.create( contentDocument, builderSpec );

        System.out.println( indexData.getMetadataJson() );

        JSONObject resultObject = new JSONObject( indexData.getMetadataJson() );

        final String approvedSectionsFieldName = QueryFieldNameResolver.getSectionKeysApprovedQueryFieldName();
        assertTrue( resultObject.has( approvedSectionsFieldName ) );
        JSONArray approvedSections = resultObject.getJSONArray( approvedSectionsFieldName );
        assertEquals( 2, approvedSections.length() );

        final String unApprovedSectionsFieldName = QueryFieldNameResolver.getSectionKeysUnapprovedQueryFieldName();
        assertTrue( resultObject.has( approvedSectionsFieldName ) );
        JSONArray unApprovedSections = resultObject.getJSONArray( unApprovedSectionsFieldName );
        assertEquals( 1, unApprovedSections.length() );
    }

    private SectionContentEntity createSectionContent( SiteEntity site, ContentEntity content, int sectionKey, boolean approved )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setSite( site );
        menuItem.setKey( new MenuItemKey( sectionKey ) );
        menuItem.setName( "menu" + sectionKey );

        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( sectionKey ) );
        sectionContent.setContent( content );
        sectionContent.setMenuItem( menuItem );
        sectionContent.setApproved( approved );
        return sectionContent;
    }

    private List<String> getKeysAsList( String indexData )
    {

        final String[] split = StringUtils.split( indexData, "," );
        List<String> keys = new ArrayList<String>();

        for ( int i = 0; i < split.length; i++ )
        {
            final String keyValue = split[i];

            final String cleaned = keyValue.replace( "\"", "" ).replace( "{", "" ).replace( "}", "" );

            final String[] keyValueArray = StringUtils.split( cleaned, ":" );

            keys.add( keyValueArray[0] );
        }

        return keys;
    }

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
