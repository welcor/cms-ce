package com.enonic.cms.itest.search;

import org.elasticsearch.action.admin.cluster.node.shutdown.NodesShutdownRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.ContentIndexServiceImpl;
import com.enonic.cms.core.search.ElasticSearchIndexServiceImpl;
import com.enonic.cms.core.search.IndexMappingProvider;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.NodeSettingsBuilder;
import com.enonic.cms.core.search.query.ContentDocument;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "itest")
@ContextConfiguration("classpath:com/enonic/cms/itest/base-core-test-context.xml")
public class ElasticSearchServiceTest
{
    @Autowired
    private ElasticSearchIndexServiceImpl elasticSearchIndexService;

    @Autowired
    private IndexMappingProvider indexMappingProvider;

    private Client client;

    @Autowired
    private NodeSettingsBuilder nodeSettingsBuilder;

    @Autowired
    private ContentIndexServiceImpl contentIndexService;

    //@Test
    public void testInitializeIndex()
        throws Exception
    {
        initIndex();

        indexContent( 1 );
        indexContent( 2 );
        indexContent( 3 );
        indexContent( 4 );
        indexContent( 5 );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "title = 'test'" );
        query.setCount( Integer.MAX_VALUE );
        contentIndexService.query( query );

        final NodesShutdownRequest nodesShutdown = new NodesShutdownRequest();
        nodesShutdown.masterNodeTimeout( TimeValue.timeValueSeconds( 0 ) );
        client.admin().cluster().nodesShutdown( nodesShutdown.exit( false ) ).actionGet();
        client.close();
    }

    private void initIndex()
        throws Exception
    {
        this.client = createClient();
        elasticSearchIndexService.setClient( client );
        final boolean indexExists = elasticSearchIndexService.indexExists( ContentIndexServiceImpl.CONTENT_INDEX_NAME );

        if ( indexExists )
        {
            elasticSearchIndexService.deleteIndex( ContentIndexServiceImpl.CONTENT_INDEX_NAME );
        }

        elasticSearchIndexService.createIndex( ContentIndexServiceImpl.CONTENT_INDEX_NAME );
        addMapping();
    }

    private Client createClient() {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
        Node node;

        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        node = NodeBuilder.nodeBuilder().settings( settings ).build();
        node.start();
        return node.client();
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

    private void flushIndex()
    {
        this.contentIndexService.flush();
    }

    private void indexContent( int contentKey )
    {
        ContentDocument contentDoc = new ContentDocument( new ContentKey( contentKey ) );
        contentDoc.setTitle( "test" );
        contentDoc.setContentTypeKey( new ContentTypeKey( 1 ) );
        contentDoc.setContentTypeName( "testContentType" );

        contentIndexService.index( contentDoc );
    }

}
