package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 12:41 PM
 */
public class ContentIndexServiceImpl_querySectionsTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testSectionFilterStatus()
        throws Exception
    {
        final ContentKey contentKey = new ContentKey( 1 );
        ContentDocument doc1 = createTestContentWithSections( contentKey );

        contentIndexService.index( doc1 );
        flushIndex();

        final ContentIndexQuery query = new ContentIndexQuery( "" );

        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( new MenuItemKey( 1 ) );
        query.setSectionFilter( Lists.newArrayList( menuItem ), ContentIndexQuery.SectionFilterStatus.APPROVED_ONLY );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( query ) );

        menuItem.setKey( new MenuItemKey( 2 ) );
        query.setSectionFilter( Lists.newArrayList( menuItem ), ContentIndexQuery.SectionFilterStatus.APPROVED_ONLY );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( query ) );

        menuItem.setKey( new MenuItemKey( 3 ) );
        query.setSectionFilter( Lists.newArrayList( menuItem ), ContentIndexQuery.SectionFilterStatus.APPROVED_ONLY );
        assertTrue( contentIndexService.query( query ).getTotalCount() == 0 );

        query.setSectionFilter( Lists.newArrayList( menuItem ), ContentIndexQuery.SectionFilterStatus.UNAPPROVED_ONLY );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( query ) );

        menuItem.setKey( new MenuItemKey( 2 ) );
        query.setSectionFilter( Lists.newArrayList( menuItem ), ContentIndexQuery.SectionFilterStatus.UNAPPROVED_ONLY );
        assertTrue( contentIndexService.query( query ).getTotalCount() == 0 );

        menuItem.setKey( new MenuItemKey( 3 ) );
        query.setSectionFilter( Lists.newArrayList( menuItem ), ContentIndexQuery.SectionFilterStatus.ANY );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( query ) );
    }


    private ContentDocument createTestContentWithSections( ContentKey contentKey )
        throws Exception
    {
        ContentDocument contentDocument = createTestContent();

        ContentEntity content = new ContentEntity();
        content.setKey( contentKey );

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

        return contentDocument;
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

        return content;
    }

}
