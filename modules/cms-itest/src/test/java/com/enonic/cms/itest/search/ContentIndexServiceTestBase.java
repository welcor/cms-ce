package com.enonic.cms.itest.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexFieldSet;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.IndexType;

import static junit.framework.Assert.assertEquals;
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
    protected final ElasticSearchTestInstance server = ElasticSearchTestInstance.getInstance();

    protected final static String[] REQUIRED_ORDERBY_FIELDS =
        new String[]{"orderby_category_key", "orderby_contenttype", "orderby_contenttype_key", "orderby_key", "orderby_priority",
            "orderby_publishfrom", "orderby_publishto", "orderby_status", "orderby_title"};

    protected final static String[] REQUIRED_STANDARD_FIELD =
        new String[]{"category_key", "category_key_numeric", "contenttype", "contenttype_key", "contenttype_key_numeric", "key",
            "key_numeric", "priority", "priority_numeric", "publishfrom", "publishto", "publishto_numeric", "status", "status_numeric",
            "title", "title._tokenized"};

    //@Autowired
    //private ContentIndexService contentIndexService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected ContentIndexService service;

    @Before
    public void initIndex()
        throws Exception
    {
        this.server.deleteIndex();

        this.service.createIndex();

        // Let the index be created properly before continuing
        Thread.sleep( 1000 );

    }


    protected IndexDataCreator indexDataCreator = new IndexDataCreator();

    protected String getMappingFromFile( String indexName, String indexType )
        throws IOException
    {
        InputStream stream = ElasticSearchTestInstance.class.getResourceAsStream( createMappingFileName( indexName, indexType ) );

        if ( stream == null )
        {
            throw new IOException( "File not found: " + createMappingFileName( indexName, indexType ) );
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy( stream, writer, "UTF-8" );
        return writer.toString();
    }

    protected String createMappingFileName( String indexName, String indexType )
    {
        return indexName + "_" + indexType + "_mapping.json";
    }

    private void verifyStandardValuesForAllIndexes( ContentDocument doc1, List<ContentIndexEntity> indexes )
    {
        for ( ContentIndexEntity contentIndexEntity : indexes )
        {
            checkContentIndexEntity( contentIndexEntity, doc1 );
        }
    }

    private ContentDocument createContentDocWithNoUserFields( ContentKey contentKey )
    {
        ContentDocument doc1 = new ContentDocument( contentKey );
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        return doc1;
    }

    private void checkUserDefinedFields( List<ContentIndexEntity> indexes, Collection<UserDefinedField> userDefinedFields,
                                         int numberOfUserDefinedFields, int numberOfUniqueUserDefinedFields )
    {
        HashSet<String> foundPaths = new HashSet<String>();
        int count = 0;

        for ( UserDefinedField userDefinedField : userDefinedFields )
        {
            boolean found = false;
            for ( ContentIndexEntity contentIndexEntity : indexes )
            {
                String path = convertToOriginalFormat( contentIndexEntity.getPath() );
                String value = contentIndexEntity.getValue();
                if ( userDefinedField.getValue().getText().length() > ContentIndexFieldSet.SPLIT_TRESHOLD )
                {
                    if ( userDefinedField.getName().equals( path ) )
                    {
                        BigText userDefinedValue = new BigText( userDefinedField.getValue().getText() );
                        if ( userDefinedValue.getText().toLowerCase().contains( value ) )
                        {
                            found = true;
                            foundPaths.add( path );
                            count++;
                        }
                    }
                }
                else
                {
                    if ( userDefinedField.getName().equals( path ) && userDefinedField.getValue().getText().equalsIgnoreCase( value ) )
                    {
                        found = true;
                        foundPaths.add( path );
                        count++;
                    }
                }
            }
            assertTrue( "Wrong value for index with path " + userDefinedField.getName(), found );
        }
        assertEquals( count, numberOfUserDefinedFields );
        assertEquals( foundPaths.size(), numberOfUniqueUserDefinedFields );

    }

    private String convertToOriginalFormat( String path )
    {
        return path.replaceAll( "#", "/" );
    }

    private void checkContentIndexEntity( ContentIndexEntity actual, ContentDocument expected )
    {
        assertEquals( expected.getCategoryKey(), actual.getCategoryKey() );
        assertEquals( expected.getContentKey(), actual.getContentKey() );
        assertEquals( expected.getContentTypeKey().toInt(), actual.getContentTypeKey() );
        assertEquals( expected.getPublishFrom(), actual.getContentPublishFrom() );
        assertEquals( expected.getPublishTo(), actual.getContentPublishTo() );
        assertEquals( expected.getStatus().intValue(), actual.getContentStatus() );

        if ( actual.getPath().equals( "owner#qualifiedname" ) )
        {
            if ( expected.getOwnerQualifiedName() != null )
            {
                assertEquals( expected.getOwnerQualifiedName().toString(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }
        }

        if ( actual.getPath().equals( "owner#key" ) )
        {
            if ( expected.getOwnerKey() != null )
            {
                assertEquals( expected.getOwnerKey().toString(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }

        }

        if ( actual.getPath().equals( "modifier#qualifiedname" ) )
        {
            if ( expected.getModifierQualifiedName() != null )
            {
                assertEquals( expected.getModifierQualifiedName().toString(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }
        }

        if ( actual.getPath().equals( "modifier#key" ) )
        {
            if ( expected.getModifierKey() != null )
            {
                assertEquals( expected.getModifierKey().toString(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }
        }

        if ( actual.getPath().equals( "priority" ) )
        {
            assertEquals( expected.getPriority().toString(), actual.getValue() );
        }

        if ( actual.getPath().equals( "created" ) )
        {
            if ( expected.getCreated() != null )
            {
                assertEquals( expected.getCreated().toString(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }
        }

        if ( actual.getPath().equals( "title" ) )
        {
            if ( expected.getTitle() != null )
            {
                assertEquals( expected.getTitle().toString().toLowerCase(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }
        }

        if ( actual.getPath().equals( "contenttype" ) )
        {
            assertEquals( expected.getContentTypeName().toString().toLowerCase(), actual.getValue() );
        }

        if ( actual.getPath().equals( "timestamp" ) )
        {
            if ( expected.getTimestamp() != null )
            {
                assertEquals( expected.getTimestamp().toString(), actual.getValue() );
            }
            else
            {
                assertEquals( "#", actual.getValue() );
            }
        }
        if ( actual.getPath().equals( "fulltext" ) )
        {
            assertTrue( expected.getBinaryExtractedText().getText().toLowerCase().contains( actual.getValue() ) );
        }

        // order value
        // num value

    }

    private void assertContentResultSetEquals( int[] contentKeys, ContentResultSet result )
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

    private void assertException( Class expectedClass, String expectedMessageStartWith, Exception actual )
    {
        assertTrue( "Expected " + expectedClass, actual.getClass().isAssignableFrom( expectedClass ) );
        assertTrue( "Expected message to starts with '" + expectedMessageStartWith + "', was: " + actual.getMessage(),
                    actual.getMessage().startsWith( expectedMessageStartWith ) );
    }

    private List<CategoryKey> createCategoryKeyList( Integer... array )
    {
        List<CategoryKey> keys = new ArrayList<CategoryKey>();
        for ( int x : array )
        {
            keys.add( new CategoryKey( x ) );
        }
        return keys;
    }

    private List<ContentTypeKey> createContentTypeList( Integer... array )
    {
        List<ContentTypeKey> keys = new ArrayList<ContentTypeKey>();
        for ( int x : array )
        {
            keys.add( new ContentTypeKey( x ) );
        }
        return keys;
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

    protected void doIndexContentDocuments( List<ContentDocument> docs )
    {
        for ( ContentDocument doc : docs )
        {
            this.service.index( doc, false );
        }

    }

    protected Map<String, SearchHitField> getFieldMapForId( ContentKey contentKey )
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

        SearchRequest req = new SearchRequest( "cms" ).types( IndexType.Content.toString() ).source( termQuery );
        SearchResponse result = server.client.search( req ).actionGet();

        assertEquals( "Should get one hit only", 1, result.getHits().getHits().length );

        SearchHit hit = result.getHits().getAt( 0 );

        return hit.getFields();

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

        SearchRequest req = new SearchRequest( "cms" ).types( IndexType.Content.toString() ).source( termQuery );
        SearchResponse result = server.client.search( req ).actionGet();

        System.out.println( "\n\n------------------------------------------" );

        for ( SearchHit hit : result.getHits() )
        {
            System.out.println( "HIT:  id = " + hit.getId() );

            final Map<String, SearchHitField> fields = hit.getFields();

            for ( String field : fields.keySet() )
            {
                System.out.println( "    " + field + " : " + fields.get( field ).value() );

            }
        }

        System.out.println( "\n\n\n\n" );

    }

    protected void letTheIndexFinishItsWork()
    {
        try
        {
            Thread.sleep( 1000 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void verifyStandardValues( ContentDocument doc1, ContentResultSet contentResultSet )
    {
        final List<ContentKey> contentKeys = contentResultSet.getKeys();

        for ( ContentKey key : contentKeys )
        {
            final Map<String, SearchHitField> fieldMapForId = getFieldMapForId( key );

            verifyStandardFields( key.toString(), fieldMapForId, doc1 );


        }

        //    for ( ContentIndexEntity contentIndexEntity : indexes )
        //    {
        //        checkContentIndexEntity( contentIndexEntity, doc1 );
        //    }

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
        compareValues( fieldMapForId.get( "category_key" ), doc.getCategoryKey().toString() );
        compareValues( fieldMapForId.get( "category_key_numeric" ), doc.getCategoryKey().toString() );
        compareValues( fieldMapForId.get( "status" ), doc.getStatus() );
        compareValues( fieldMapForId.get( "status_numeric" ), doc.getStatus() );
    }

    private void compareValues( SearchHitField field, String expected )
    {
        Object actual = field.getValue();

        final String failureMessage = "Error in field value for field: " + field.getName();

        if ( actual instanceof Double )
        {
            assertEquals( failureMessage, new Double( expected ), actual );
        }
        else if ( actual instanceof String )
        {
            try
            {
                Double actualAsDouble = Double.parseDouble( (String) actual );
                assertEquals( failureMessage, new Double( expected ), actualAsDouble );
            }
            catch ( NumberFormatException e )
            {
                assertEquals( failureMessage, expected, actual );
            }
        }
        else
        {
            fail( "Unexpected value for " + field.getName() );
        }
    }

    private void compareValues( SearchHitField field, int expected )
    {
        compareValues( field, Integer.toString( expected ) );
    }

}
