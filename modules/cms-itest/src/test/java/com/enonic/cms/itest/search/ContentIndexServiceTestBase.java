package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.ContentIndexServiceImpl;
import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexMappingProvider;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.SimpleText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/20/11
 * Time: 3:35 PM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "itest")
@ContextConfiguration("classpath:com/enonic/cms/itest/base-core-test-context.xml")
public abstract class ContentIndexServiceTestBase
{
    private IndexMappingProvider indexMappingProvider;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected ContentIndexServiceImpl contentIndexService;

    @Autowired
    protected ElasticSearchIndexService elasticSearchIndexService;

    @Before
    public void initIndex()
        throws Exception
    {
        final ClusterHealthResponse clusterHealth =
            elasticSearchIndexService.getClusterHealth( ContentIndexServiceImpl.CONTENT_INDEX_NAME, true );

        final boolean indexExists = elasticSearchIndexService.indexExists( ContentIndexServiceImpl.CONTENT_INDEX_NAME );

        if ( indexExists )
        {
            elasticSearchIndexService.deleteIndex( ContentIndexServiceImpl.CONTENT_INDEX_NAME );
        }

        elasticSearchIndexService.getClusterHealth( ContentIndexServiceImpl.CONTENT_INDEX_NAME, true );

        elasticSearchIndexService.createIndex( ContentIndexServiceImpl.CONTENT_INDEX_NAME );

        elasticSearchIndexService.getClusterHealth( ContentIndexServiceImpl.CONTENT_INDEX_NAME, true );

        addMapping();

        elasticSearchIndexService.getClusterHealth( ContentIndexServiceImpl.CONTENT_INDEX_NAME, true );
    }

    private void addMapping()
    {
        doAddMapping( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content );
        doAddMapping( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Binaries );
    }

    private void doAddMapping( String indexName, IndexType indexType )
    {
        String mapping = indexMappingProvider.getMapping( indexName, indexType.toString() );
        elasticSearchIndexService.putMapping( ContentIndexServiceImpl.CONTENT_INDEX_NAME, indexType.toString(), mapping );
    }

    protected void assertContentResultSetEquals( int[] contentKeys, ContentResultSet result )
    {
        assertEquals( "contentResultSet length", contentKeys.length, result.getTotalCount() );

        List<ContentKey> list = result.getKeys();
        for ( int contentKey : contentKeys )
        {
            if ( !list.contains( new ContentKey( contentKey ) ) )
            {
                // ContentIndexServiceImplTest.LOG.info( "{}", contentKey );
            }

            assertTrue( "Unexpected ContentResultSet. ContentKey not found: " + contentKey, list.contains( new ContentKey( contentKey ) ) );
        }
    }

    protected Map<String, Object> getFieldMapForId( ContentKey contentKey )
    {
        SearchResponse result = fetchDocumentByContentKey( contentKey );

        assertEquals( "Should get one hit only", 1, result.getHits().getHits().length );

        SearchHit hit = result.getHits().getAt( 0 );

        return hit.getSource();
    }

    private SearchResponse fetchDocumentByContentKey( ContentKey contentKey )
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + 100 + ",\n" +
            //   "\"fields\" : [\"*\"],\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"key\" : \"" + new Long( contentKey.toString() ).toString() + "\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        return elasticSearchIndexService.search( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content.toString(), termQuery );
    }

    protected void printAllIndexContent()
    {

        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 100,\n" +
            "\"fields\" : [\"_source\"],\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchResponse result =
            elasticSearchIndexService.search( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content.toString(), termQuery );

        System.out.println( "\n\n---------- CONTENT --------------------------------" );
        System.out.println( result.toString() );
        System.out.println( "\n\n" );
        result = elasticSearchIndexService.search( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Binaries.toString(), termQuery );

        System.out.println( "\n\n---------- BINARIES --------------------------------" );
        System.out.println( result.toString() );
        System.out.println( "\n\n\n\n" );

    }

    protected void flushIndex()
    {
        this.contentIndexService.flush();
    }

    protected ContentDocument createContentDocument( int contentKey, String title, String preface, String fulltext )
    {
        return createContentDocument( contentKey, title, new String[][]{{"data/preface", preface}, {"fulltext", fulltext}} );
    }

    protected ContentDocument createContentDocumentWithTextField( int contentKey, String title, String preface, String fulltext )
    {
        return createContentDocument( contentKey, title, new String[][]{{"data/preface", preface}, {"data/textfield", fulltext}} );
    }

    protected ContentDocument createContentDocument( ContentKey contentKey, CategoryKey categoryKey, ContentTypeKey contentTypeKey,
                                                     int status, String title, String[][] fields )
    {
        ContentDocument doc = new ContentDocument( contentKey );
        doc.setCategoryKey( categoryKey );
        doc.setContentTypeKey( contentTypeKey );
        doc.setContentTypeName( "Article" );
        if ( title != null )
        {
            doc.setTitle( title );
        }
        if ( fields != null )
        {
            for ( String[] field : fields )
            {
                doc.addUserDefinedField( field[0], field[1] );
            }
        }
        doc.setStatus( status );
        doc.setPriority( 0 );
        return doc;
    }


    ContentDocument createContentDocument( int contentKey, String title, String[][] fields )
    {
        ContentDocument doc = new ContentDocument( new ContentKey( contentKey ) );
        doc.setCategoryKey( new CategoryKey( 9 ) );
        doc.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc.setContentTypeName( "Article" );
        if ( title != null )
        {
            doc.setTitle( title );
        }
        if ( fields != null )
        {
            for ( String[] field : fields )
            {
                doc.addUserDefinedField( field[0], field[1] );
            }
        }
        doc.setStatus( 2 );
        doc.setPriority( 0 );
        return doc;
    }


    protected void setUpStandardTestValues()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        // Index content 1, 2 og 3:
        ContentDocument doc1 = new ContentDocument( new ContentKey( 1322 ) );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        doc1.addUserDefinedField( "data/person/age", new SimpleText( "38" ) );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/description", "description1-1" );
        doc1.addUserDefinedField( "data/person/birthdate", new SimpleText( "1975-05-05" ) );

        doc1.addUserDefinedField( "data/person/age", new SimpleText( "39" ) );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/description", "description1-2" );
        doc1.addUserDefinedField( "data/person/birthdate", new SimpleText( "1976-06-06" ) );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        doc1.setLanguageCode( "en" );
        contentIndexService.index( doc1 );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc2 = new ContentDocument( new ContentKey( 1327 ) );
        doc2.setCategoryKey( new CategoryKey( 7 ) );
        doc2.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc2.setContentTypeName( "Adults" );
        doc2.setTitle( "Fry" );
        doc2.addUserDefinedField( "data/person/age", new SimpleText( "28" ) );
        doc2.addUserDefinedField( "data/person/gender", "male" );
        doc2.addUserDefinedField( "data/person/description", "description2" );
        doc2.addUserDefinedField( "data/person/birthdate", new SimpleText( "1978-08-01" ) );
        // Publish from February 29th to March 29th.
        doc2.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc2.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc2.setStatus( 2 );
        doc2.setPriority( 0 );
        doc2.setLanguageCode( "fr" );
        contentIndexService.index( doc2 );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc3 = new ContentDocument( new ContentKey( 1323 ) );
        doc3.setCategoryKey( new CategoryKey( 9 ) );
        doc3.setContentTypeKey( new ContentTypeKey( 37 ) );
        doc3.setContentTypeName( "Children" );
        doc3.setTitle( "Bart" );
        doc3.addUserDefinedField( "data/person/age", new SimpleText( "10" ) );
        doc3.addUserDefinedField( "data/person/gender", "male" );
        doc3.addUserDefinedField( "data/person/description", "description3" );
        doc3.addUserDefinedField( "data/person/birthdate", new SimpleText( "2003-03-28" ) );
        // Publish from March 1st to April 1st
        doc3.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc3.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc3.setStatus( 2 );
        doc3.setPriority( 0 );
        doc3.setLanguageCode( "en" );
        contentIndexService.index( doc3 );

        ContentDocument doc4 = new ContentDocument( new ContentKey( 1324 ) );
        doc4.setCategoryKey( new CategoryKey( 9 ) );
        doc4.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc4.setContentTypeName( "Adults" );
        doc4.setTitle( "Bender" );
        doc4.addUserDefinedField( "data/person/age", new SimpleText( "5" ) );
        doc4.addUserDefinedField( "data/person/gender", "man-bot" );
        doc4.addUserDefinedField( "data/person/description", "description4" );
        doc3.addUserDefinedField( "data/person/birthdate", new SimpleText( "2010-05-22" ) );
        // Publish from March 1st to March 28th.
        doc4.setPublishFrom( date.getTime() );
        date.add( Calendar.DAY_OF_MONTH, 27 );
        doc4.setPublishTo( date.getTime() );
        doc4.setStatus( 2 );
        doc4.setPriority( 0 );
        doc4.setLanguageCode( "en" );
        contentIndexService.index( doc4 );

        flushIndex();
    }

    protected ContentDocument createContentDocument( ContentKey contentKey, CategoryKey categoryKey, ContentTypeKey contentTypeKey,
                                                     String title, List<UserDefinedField> userDefinedFields )
    {
        ContentDocument doc = new ContentDocument( contentKey );
        doc.setCategoryKey( categoryKey );
        doc.setContentTypeKey( contentTypeKey );
        doc.setContentTypeName( "Article" );

        if ( title != null )
        {
            doc.setTitle( title );
        }

        for ( UserDefinedField userDefinedField : userDefinedFields )
        {
            doc.addUserDefinedField( userDefinedField );
        }

        doc.setStatus( 2 );
        doc.setPriority( 0 );
        return doc;
    }

    protected void verifyField( String fieldName, int expected, Map<String, Object> fieldMapForId )
    {
        final Object hits = fieldMapForId.get( fieldName );

        if ( expected > 1 )
        {
            assertNotNull( "Hits is null for field: " + fieldName, hits );

            assertTrue( "Hits should be a collection", hits instanceof Collection );

            Assert.assertEquals( "Wrong number of hits for field: " + fieldName, expected, ( (Collection) hits ).size() );

        }
        else if ( expected == 1 )
        {
            assertNotNull( "Hits is null for field: " + fieldName, hits );

            if ( hits instanceof Collection )
            {
                Assert.assertEquals( "Wrong number of hits for field: " + fieldName, expected, ( (Collection) hits ).size() );
            }

        }
        else
        {
            assertNull( "Should be null", hits );
        }
    }


    @Autowired
    public void setIndexMappingProvider( IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }
}
