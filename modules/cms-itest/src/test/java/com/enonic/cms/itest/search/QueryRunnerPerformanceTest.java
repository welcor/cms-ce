package com.enonic.cms.itest.search;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.search.IndexType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/21/11
 * Time: 9:29 AM
 */
public class QueryRunnerPerformanceTest
    extends ContentIndexServiceTestBase
{
    protected static final int NUMBER_OF_ENTRIES = 5000;

    Random random = new SecureRandom();

    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    List<ContentDocument> docs;

    @Before
    public void initIndexIfNotDone()
        throws Exception
    {

        if ( !this.service.isIndexed( new ContentKey( 1 ) ) )
        {
            docs = IndexDataCreator.createContentDocuments( 0, NUMBER_OF_ENTRIES, "Adult" );

            System.out.println( "Indexing data" );
            this.service.indexBulk( docs );
            this.service.optimize();

            Thread.sleep( 1000 );

            final ImmutableMap<String, String> settingsMap = this.server.node.settings().getAsMap();

            for ( String setting : settingsMap.keySet() )
            {
                System.out.println( setting + " : " + settingsMap.get( setting ) );
            }
        }
    }

    @Ignore
    @Test
    public void initializeStuff()
    {
        // Do nothing, just run the Before
    }

    @Ignore
    @Test
    @PerfTest(invocations = 50, threads = 5)
    @Required(max = 1500, average = 200)
    public void term_query_test()
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 10,\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"key_numeric\" : " + random.nextInt( NUMBER_OF_ENTRIES - 1 ) +
            "    }\n" +
            "  }\n" +
            "}";

        SearchRequest req = new SearchRequest( "cms" ).types( IndexType.Content.toString() ).source( termQuery );
        server.client.search( req ).actionGet();
    }


    @Ignore
    @Test
    @PerfTest(invocations = 50, threads = 5)
    @Required(max = 1500, average = 200)
    public void id_query_test()
        throws Exception
    {
        String termQuery = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 10,\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"key_numeric\" : " + random.nextInt( NUMBER_OF_ENTRIES - 1 ) +
            "    }\n" +
            "  }\n" +
            "}";

        SearchRequest req = Requests.searchRequest( "cms" ).types( IndexType.Content.toString() ).source( termQuery );
        server.client.search( req ).actionGet();
    }



    @Ignore
    @Test
    @PerfTest(invocations = 50, threads = 5)
    @Required(max = 1500, average = 200)
    public void id_get_test()
        throws Exception
    {
        GetRequest req = Requests.getRequest( "cms" ).id( random.nextInt( NUMBER_OF_ENTRIES - 1 ) + "" );
        server.client.get( req ).actionGet();
    }


}
