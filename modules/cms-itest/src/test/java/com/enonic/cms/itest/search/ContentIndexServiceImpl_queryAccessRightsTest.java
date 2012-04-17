package com.enonic.cms.itest.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_queryAccessRightsTest
    extends ContentIndexServiceTestBase
{

    private GroupEntity groupA;

    private GroupEntity groupB;

    @Before
    public void setUp()
    {
        groupA = new GroupEntity();
        groupA.setKey( new GroupKey( "groupA" ) );
        groupA.setName( "group A" );

        groupB = new GroupEntity();
        groupB.setKey( new GroupKey( "groupB" ) );
        groupB.setName( "group B" );
    }

    @Test
    public void query_access_rights_allowed()
    {
        // Setup standard values
        setUpTestValues();
        flushIndex();

        final ImmutableSet<GroupKey> filterGroupA = ImmutableSet.of( groupA.getGroupKey() );
        final ImmutableSet<GroupKey> filterGroupB = ImmutableSet.of( groupB.getGroupKey() );
        final ImmutableSet<GroupKey> filterGroupAB = ImmutableSet.of( groupA.getGroupKey(), groupB.getGroupKey() );

        // accessible for group A
        ContentIndexQuery query = createQuery( "key = 1322" );
        query.setSecurityFilter( filterGroupA );
        ContentResultSet res2 = contentIndexService.query( query );
        assertEquals( 1, res2.getLength() );

        query = createQuery( "key = 1322" );
        query.setSecurityFilter( filterGroupAB );
        ContentResultSet res3 = contentIndexService.query( query );
        assertEquals( 1, res3.getLength() );

        // accessible for group B
        query = createQuery( "key = 1327" );
        query.setSecurityFilter( filterGroupB );
        ContentResultSet res4 = contentIndexService.query( query );
        assertEquals( 1, res4.getLength() );

        query = createQuery( "key = 1327" );
        query.setSecurityFilter( filterGroupAB );
        ContentResultSet res5 = contentIndexService.query( query );
        assertEquals( 1, res5.getLength() );

        // accessible for group A or B
        query = createQuery( "key = 1323" );
        query.setSecurityFilter( filterGroupA );
        ContentResultSet res6 = contentIndexService.query( query );
        assertEquals( 1, res6.getLength() );

        query = createQuery( "key = 1323" );
        query.setSecurityFilter( filterGroupB );
        ContentResultSet res7 = contentIndexService.query( query );
        assertEquals( 1, res7.getLength() );

        query = createQuery( "key = 1323" );
        query.setSecurityFilter( filterGroupAB );
        ContentResultSet res8 = contentIndexService.query( query );
        assertEquals( 1, res8.getLength() );

        query = createQuery( "key > 1320" );
        query.setSecurityFilter( filterGroupAB );
        ContentResultSet res11 = contentIndexService.query( query );
        assertEquals( 3, res11.getLength() );

        // accessible for group B (2 contents)
        query = createQuery( "key <= 1327" );
        query.setSecurityFilter( filterGroupB );
        ContentResultSet res9 = contentIndexService.query( query );
        assertEquals( 2, res9.getLength() );

        // accessible for group A (2 contents)
        query = createQuery( "key > 1320" );
        query.setSecurityFilter( filterGroupB );
        ContentResultSet res10 = contentIndexService.query( query );
        assertEquals( 2, res10.getLength() );
    }

    @Test
    public void query_access_rights_restricted()
    {
        // Setup standard values
        setUpTestValues();
        flushIndex();

        final ImmutableSet<GroupKey> filterGroupA = ImmutableSet.of( groupA.getGroupKey() );
        final ImmutableSet<GroupKey> filterGroupB = ImmutableSet.of( groupB.getGroupKey() );
        final ImmutableSet<GroupKey> filterGroupAB = ImmutableSet.of( groupA.getGroupKey(), groupB.getGroupKey() );

        ContentIndexQuery query = createQuery( "key = 1321" );
        query.setSecurityFilter( filterGroupAB );
        ContentResultSet res1 = contentIndexService.query( query );
        assertEquals( 0, res1.getLength() );

        // accessible for group A
        query = createQuery( "key = 1322" );
        query.setSecurityFilter( filterGroupB );
        ContentResultSet res2 = contentIndexService.query( query );
        assertEquals( 0, res2.getLength() );

        // accessible for group B
        query = createQuery( "key = 1327" );
        query.setSecurityFilter( filterGroupA );
        ContentResultSet res4 = contentIndexService.query( query );
        assertEquals( 0, res4.getLength() );

        // not accessible
        query = createQuery( "key = 1324" );
        query.setSecurityFilter( filterGroupAB );
        ContentResultSet res6 = contentIndexService.query( query );
        assertEquals( 0, res6.getLength() );
    }

    private ContentIndexQuery createQuery( String queryString )
    {
        ContentIndexQuery query = new ContentIndexQuery( queryString );
        query.setCount( 10 );
        return query;
    }

    private void setUpTestValues()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        // Index content 1, 2 og 3:
        final ContentDocument doc1 = new ContentDocument( new ContentKey( 1322 ) );
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
        setAccessRightsForContent( doc1, groupA );
        contentIndexService.index( doc1, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        final ContentDocument doc2 = new ContentDocument( new ContentKey( 1327 ) );
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
        setAccessRightsForContent( doc2, groupB );
        contentIndexService.index( doc2, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        final ContentDocument doc3 = new ContentDocument( new ContentKey( 1323 ) );
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
        setAccessRightsForContent( doc3, groupA, groupB );
        contentIndexService.index( doc3, true );

        final ContentDocument doc4 = new ContentDocument( new ContentKey( 1324 ) );
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
        contentIndexService.index( doc4, true );

        flushIndex();
    }

    private void setAccessRightsForContent( ContentDocument content, GroupEntity... groups )
    {
        final List<ContentAccessEntity> accessRights = new ArrayList<ContentAccessEntity>();

        for ( GroupEntity group : groups )
        {
            final ContentAccessEntity contentAccessGroup = new ContentAccessEntity();
            contentAccessGroup.setGroup( group );
            contentAccessGroup.setReadAccess( true );
            contentAccessGroup.setUpdateAccess( true );
            contentAccessGroup.setDeleteAccess( true );

            contentAccessGroup.setKey( content.getContentKey().toString() );

            accessRights.add( contentAccessGroup );
        }

        content.addContentAccessRights( accessRights );
    }
}
