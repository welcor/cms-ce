package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/29/11
 * Time: 3:11 PM
 */
public class ContentIndexServiceImpl_queryIndexAndCountTest
    extends ContentIndexServiceTestHibernatedBase
{

    private static final DateTime DATE_TIME_2010_01_01 = new DateTime( 2010, 1, 1, 0, 0, 0, 0 );

    @Before
    public void setUp()
    {

        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        MockHttpServletRequest httpRequest = new MockHttpServletRequest( "GET", "/" );
        ServletRequestAccessor.setRequest( httpRequest );

        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContent", "myTitle" );
        ctyconf.startBlock( "MyContent" );
        ctyconf.addInput( "myTitle", "text", "contentdata/my-title", "Title", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save(
            factory.createCategory( "MyCategory", null, "MyContentType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-querier", "read, admin_browse, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();
    }


    @Test
    public void test_having_one_matching_content_query_returns_one_when_index_is_0_and_count_1()
        throws Exception
    {
        //service.deleteIndex();

        final CategoryKey categoryKey = new CategoryKey( 1 );

        ContentDocument doc = createContentDocument( 1, "a-1", categoryKey );
        contentIndexService.index( doc );
        ContentDocument doc1 = createContentDocument( 2, "c-1", categoryKey );
        contentIndexService.index( doc1 );

        contentIndexService.optimize();

        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        // query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 0 );
        query.setCount( 1 );
        query.setCategoryFilter( Lists.newArrayList( categoryKey ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 1, result.getLength() );
        assertEquals( 1, result.getTotalCount() );
    }

    @Test
    public void test_having_one_matching_content_query_returns_none_when_index_is_1_and_count_1()
        throws Exception
    {
        // setup
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        flushIndex();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 1 );
        // query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 0, result.getLength() );
        assertEquals( 1, result.getTotalCount() );
    }

    @Test
    public void test_having_two_matching_content_query_returns_one_when_index_is_1_and_count_1()
        throws Exception
    {

        // setup
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-2", "c-2", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        flushIndex();

        // exercise
        //ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        ContentIndexQuery query = new ContentIndexQuery( "" );
        //query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 2 );
        //query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        System.out.println( "Hits length: " + result.getLength() );
        System.out.println( "TotalCount: " + result.getTotalCount() );

        // verify
        assertEquals( 1, result.getLength() );
        assertEquals( 2, result.getTotalCount() );
    }

    @Test
    public void having_three_matching_content_query_returns_two_when_index_is_1_and_count_2()
        throws Exception
    {
        // setup
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        fixture.flushIndexTransaction();
        contentService.createContent( createContentCommand( "c-2", "c-2", "MyCategory" ) );
        fixture.flushIndexTransaction();
        contentService.createContent( createContentCommand( "c-3", "c-3", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        flushIndex();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        //ContentIndexQuery query = new ContentIndexQuery( "" );
        //query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 2 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 2, result.getLength() );
        assertEquals( 3, result.getTotalCount() );
    }

    @Test
    public void having_three_matching_content_query_returns_one_when_index_is_1_and_count_1()
        throws Exception
    {
        // setup
        contentService.createContent( createContentCommand( "a-1", "a-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-2", "c-2", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-3", "c-3", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        flushIndex();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        //ContentIndexQuery query = new ContentIndexQuery( "" );
        //query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 1 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 3, result.getTotalCount() );
        assertEquals( 1, result.getLength() );
        assertTrue( result.getContent( 0 ).getName().contains( "c-" ) );
    }


    private ContentDocument createContentDocument( int contentKey, String title, CategoryKey categoryKey )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc = new ContentDocument( new ContentKey( contentKey ) );
        doc.setCategoryKey( categoryKey );
        doc.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc.setContentTypeName( "Adults" );
        doc.setTitle( title );
        doc.addUserDefinedField( "data/person/age", "38" );
        doc.addUserDefinedField( "data/person/gender", "male" );
        doc.addUserDefinedField( "data/person/description",
                                 "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
        // Publish from February 28th to March 28th.
        doc.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc.setStatus( 2 );
        doc.setPriority( 0 );

        return doc;
    }

    private CreateContentCommand createContentCommand( String name, String title, String categoryName )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );
        contentData.add( createTextDataEntry( "myTitle", title ) );
        return createContentCommand( name, categoryName, contentData, "content-querier" );
    }

    private CreateContentCommand createContentCommand( String name, String categoryName, ContentData contentData, String creatorUid )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( ContentStatus.APPROVED );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setAvailableFrom( DATE_TIME_2010_01_01.toDate() );
        createContentCommand.setAvailableTo( null );
        createContentCommand.setContentName( name );
        return createContentCommand;
    }

    private TextDataEntry createTextDataEntry( String name, String value )
    {
        return new TextDataEntry( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig().getInputConfig( name ), value );
    }

}
