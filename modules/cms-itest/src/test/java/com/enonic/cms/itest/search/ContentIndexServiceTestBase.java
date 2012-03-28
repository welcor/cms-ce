package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.ContentIndexServiceImpl;
import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexMappingProvider;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;
import com.enonic.cms.core.search.builder.IndexFieldNameResolver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/20/11
 * Time: 3:35 PM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/enonic/cms/itest/base-core-test-context.xml")
public abstract class ContentIndexServiceTestBase
{

    private IndexMappingProvider indexMappingProvider;

    protected final static String[] REQUIRED_ORDERBY_FIELDS =
        new String[]{"orderby_categorykey", "orderby_contenttype", "orderby_contenttypekey", "orderby_key", "orderby_priority",
            "orderby_publishfrom", "orderby_status", "orderby_title"};

    protected final static String[] REQUIRED_STANDARD_FIELD =
        new String[]{"categorykey", "categorykey_numeric", "contenttype", "contenttypekey", "contenttypekey_numeric", "key", "key_numeric",
            "priority", "priority_numeric", "publishfrom", "status", "status_numeric", "title", "title._tokenized"};

    private final static Pattern SPECIAL_FIELD_PATTERN = Pattern.compile(
        "(\\" + IndexFieldNameConstants.NON_ANALYZED_FIELD_POSTFIX + "){1}$|(\\" + IndexFieldNameConstants.NUMERIC_FIELD_POSTFIX +
            "){1}$" );

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected ContentIndexService contentIndexService;

    @Autowired
    protected ElasticSearchIndexService elasticSearchIndexService;

    @Before
    public void initIndex()
        throws Exception
    {
        final boolean indexExists = elasticSearchIndexService.indexExists( ContentIndexServiceImpl.CONTENT_INDEX_NAME );

        if ( indexExists )
        {
            elasticSearchIndexService.deleteIndex( ContentIndexServiceImpl.CONTENT_INDEX_NAME );
        }

        elasticSearchIndexService.createIndex( ContentIndexServiceImpl.CONTENT_INDEX_NAME );
        addMapping();
    }

    private void addMapping()
    {
        doAddMapping( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content );
        doAddMapping( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Binaries );
    }

    private void doAddMapping( String indexName, IndexType indexType )
    {
        String mapping = indexMappingProvider.getMapping( indexName, indexType );
        elasticSearchIndexService.putMapping( ContentIndexServiceImpl.CONTENT_INDEX_NAME, indexType, mapping );
    }

    protected IndexDataCreator indexDataCreator = new IndexDataCreator();

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

    protected void doIndexContentDocuments( List<ContentDocument> docs )
    {
        for ( ContentDocument doc : docs )
        {
            this.contentIndexService.index( doc, false );
        }
    }

    protected Map<String, SearchHitField> getFieldMapForId( ContentKey contentKey )
    {
        SearchResponse result = fetchDocumentByContentKey( contentKey );

        assertEquals( "Should get one hit only", 1, result.getHits().getHits().length );

        SearchHit hit = result.getHits().getAt( 0 );

        return hit.getFields();

    }

    private SearchResponse fetchDocumentByContentKey( ContentKey contentKey )
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + 100 + ",\n" +
            "\"fields\" : [\"*\"],\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"key_numeric\" : \"" + new Long( contentKey.toString() ).toString() + "\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        return elasticSearchIndexService.search( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content, termQuery );
    }


    protected void verifyUserDefinedFields( ContentKey contentKey, ContentDocument doc1 )
    {
        SearchResponse result = fetchDocumentByContentKey( contentKey );
        assertEquals( "Should get one hit only", 1, result.getHits().getHits().length );

        final Collection<UserDefinedField> userDefinedFields = doc1.getUserDefinedFields();

        final SearchHit hit = result.getHits().getAt( 0 );
        final Map<String, SearchHitField> hitFieldMap = hit.getFields();

        for ( UserDefinedField field : userDefinedFields )
        {
            final String indexFieldName = IndexFieldNameResolver.normalizeFieldName( field.getName() );
            final SearchHitField hitField = hitFieldMap.get( indexFieldName );

            assertNotNull( "Could not find field in index: " + indexFieldName, hitField );
            compareValues( hitField, field.getValue().toString() );
        }
    }

    private boolean isUserDataField( String hitField )
    {
        if ( StringUtils.startsWith( hitField, "data_" ) )
        {
            Matcher m = SPECIAL_FIELD_PATTERN.matcher( hitField );
            if ( !m.find() )
            {
                return true;
            }
        }
        return false;
    }


    protected void printAllIndexContent()
    {

        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 100,\n" +
            "\"fields\" : [\"*\"],\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchResponse result =
            elasticSearchIndexService.search( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content, termQuery );

        System.out.println( "\n\n------------------------------------------" );
        System.out.println( result.toString() );
        System.out.println( "\n\n\n\n" );
    }

    protected void flushIndex()
    {
        this.contentIndexService.flush();
    }

    protected void verifyStandardFields( ContentDocument doc1, ContentKey contentKey )
    {
        final Map<String, SearchHitField> fieldMapForId = getFieldMapForId( contentKey );

        verifyStandardFields( contentKey.toString(), fieldMapForId, doc1 );
    }

    private void verifyStandardFields( String id, Map<String, SearchHitField> fieldMapForId, ContentDocument doc )
    {
        for ( String requiredField : REQUIRED_STANDARD_FIELD )
        {
            assertNotNull( "Missing required field: '" + requiredField + "'", fieldMapForId.get( requiredField ) );
        }

        for ( String requiredField : REQUIRED_ORDERBY_FIELDS )
        {
            assertNotNull( "Missing orderby field: '" + requiredField + "'", fieldMapForId.get( requiredField ) );
        }

        compareValues( fieldMapForId.get( "key" ), id );
        compareValues( fieldMapForId.get( "key_numeric" ), id );
        compareValues( fieldMapForId.get( "categorykey" ), doc.getCategoryKey().toString() );
        compareValues( fieldMapForId.get( "categorykey_numeric" ), doc.getCategoryKey().toString() );
        compareValues( fieldMapForId.get( "status" ), doc.getStatus() );
        compareValues( fieldMapForId.get( "status_numeric" ), doc.getStatus() );
    }

    private void compareValues( final SearchHitField field, final String expected )
    {
        final List<Object> values = field.getValues();

        if ( values.size() > 1 )
        {
            doCompareArrayValues( field.getName(), values, expected );
            return;
        }

        Object singleValue = values.get( 0 );

        final String failureMessage = "Error in field value for field: " + field.getName();

        if ( singleValue instanceof Double )
        {
            assertEquals( failureMessage, new Double( expected ), singleValue );
        }
        else if ( singleValue instanceof String )
        {
            try
            {
                Double actualAsDouble = Double.parseDouble( (String) singleValue );
                assertEquals( failureMessage, new Double( expected ), actualAsDouble );
            }
            catch ( NumberFormatException e )
            {
                assertEquals( failureMessage, StringUtils.lowerCase( expected ), StringUtils.lowerCase( (String) singleValue ) );
            }
        }
        else
        {
            fail( "Unexpected value for " + field.getName() );
        }
    }

    private void doCompareArrayValues( String fieldName, List<Object> values, String expected )
    {
        final String failureMessage = "Missing value " + expected + " in field: " + fieldName;

        assertTrue( failureMessage, values.contains( expected ) );
    }

    private void compareValues( SearchHitField field, int expected )
    {
        compareValues( field, Integer.toString( expected ) );
    }

    protected ContentDocument createContentDocument( int contentKey, String title, String preface, String fulltext )
    {
        return createContentDocument( contentKey, title, new String[][]{{"data/preface", preface}, {"fulltext", fulltext}} );
    }

    protected ContentDocument createContentDocumentWithTextField( int contentKey, String title, String preface, String fulltext )
    {
        return createContentDocument( contentKey, title, new String[][]{{"data/preface", preface}, {"data/textfield", fulltext}} );
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

    protected void setUpStandardTestValues()
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

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc2 = new ContentDocument( new ContentKey( 1327 ) );
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
        contentIndexService.index( doc2, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc3 = new ContentDocument( new ContentKey( 1323 ) );
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
        contentIndexService.index( doc3, true );

        ContentDocument doc4 = new ContentDocument( new ContentKey( 1324 ) );
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


    @Autowired
    public void setIndexMappingProvider( IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }
}
