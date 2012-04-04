package com.enonic.cms.itest.search;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexMappingProvider;
import com.enonic.cms.core.search.query.QueryTranslator;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/enonic/cms/itest/base-core-test-context.xml")
public class MappingTest

{
    @Autowired
    protected ElasticSearchIndexService elasticSearchIndexService;

    private IndexMappingProvider mappingProvider = new TestIndexMappingProvider();

    @Autowired
    QueryTranslator queryTranslator;

    @Test
    public void testStuff()
        throws Exception
    {

        final GregorianCalendar date = new GregorianCalendar( 2010, Calendar.JANUARY, 18, 01, 00 );

        elasticSearchIndexService.createIndex( "test" );

        final String mapping = mappingProvider.getMapping( "test", "testType" );
        elasticSearchIndexService.putMapping( "test", "testType", mapping );

        indexADocument( 8.0, "2" );
        indexADocument( "torsk", "1" );
        indexADocument( "sei", "3" );
        indexADocument( date.getTime(), "4" );

        elasticSearchIndexService.flush( "test" );

        printAllIndexContent();

        SearchResponse search = executeQuery( "fisk = 'torsk'" );
        assertEquals( 1, search.getHits().getHits().length );

        search = executeQuery( "fisk = '8.0'" );
        assertEquals( 1, search.getHits().getHits().length );

        search = executeQuery( "fisk = 8" );
        assertEquals( 1, search.getHits().getHits().length );

        search = executeQuery( "fisk = 8.0" );
        assertEquals( 1, search.getHits().getHits().length );

        // search = executeQuery( "fisk = date('2012-01-18 02:00:00')" );
        // assertEquals( 1, search.getHits().getHits().length );
    }


    private SearchResponse executeQuery( String queryString )
        throws Exception
    {
        ContentIndexQuery query = new ContentIndexQuery( queryString );
        final SearchSourceBuilder build = queryTranslator.build( query );

        return elasticSearchIndexService.search( "test", "testType", build );
    }

    private void indexADocument( String value, String id )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject().field( "fisk", value ).endObject();
        IndexRequest request = new IndexRequest( "test" ).type( "testType" ).id( id ).source( builder );
        elasticSearchIndexService.index( request );
    }

    private void indexADocument( Double value, String id )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject().field( "fisk", value ).field( "fisk.number", value ).endObject();

        IndexRequest request = new IndexRequest( "test" ).type( "testType" ).id( id ).source( builder );
        elasticSearchIndexService.index( request );
    }

    private void indexADocument( Date value, String id )
        throws IOException
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject().field( "fisk", value ).field( "fisk.date", value ).endObject();

        IndexRequest request = new IndexRequest( "test" ).type( "testType" ).id( id ).source( builder );
        elasticSearchIndexService.index( request );
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

        SearchResponse result = elasticSearchIndexService.search( "test", "testType", termQuery );

        System.out.println( "\n\n------------------------------------------" );
        System.out.println( result.toString() );
        System.out.println( "\n\n\n\n" );
    }


    private ContentDocument createContentDocument( int contentKey, String title, String[][] stringFields )
    {
        ContentDocument doc = new ContentDocument( new ContentKey( contentKey ) );
        doc.setCategoryKey( new CategoryKey( 9 ) );
        doc.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc.setContentTypeName( "Article" );
        if ( title != null )
        {
            doc.setTitle( title );
        }
        if ( stringFields != null )
        {
            for ( String[] field : stringFields )
            {
                doc.addUserDefinedField( field[0], field[1] );
            }
        }
        doc.setStatus( 2 );
        doc.setPriority( 0 );
        return doc;
    }


}



