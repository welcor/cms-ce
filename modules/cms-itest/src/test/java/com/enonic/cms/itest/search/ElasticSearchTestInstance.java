package com.enonic.cms.itest.search;

import java.io.File;
import java.util.logging.Logger;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/20/11
 * Time: 3:26 PM
 */
public class ElasticSearchTestInstance
    implements InitializingBean, DisposableBean
{

    private final static Logger LOG = Logger.getLogger( ElasticSearchTestInstance.class.getName() );

    protected static final String INDEX_NAME = "cms";

    protected Node node;

    protected Client client;

    private static ElasticSearchTestInstance instance;

    private final static String GATEWAY_SETTING_KEY = "gateway.type";

    private final static String GATEWAY_NO_PERSISTENCE = "none";

    public void afterPropertiesSet()
        throws Exception
    {
        instance = new ElasticSearchTestInstance();
        try
        {
            instance.start();
        }
        catch ( Exception e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void destroy()
        throws Exception
    {
        cleanUp();
    }

    private ElasticSearchTestInstance()
    {
    }

    public static ElasticSearchTestInstance getInstance()
    {
        if ( instance == null )
        {
            instance = new ElasticSearchTestInstance();
            try
            {
                instance.start();
            }
            catch ( Exception e )
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return instance;
    }


    public synchronized static void cleanUp()
        throws Exception
    {

        LOG.info( "Cleaning up and shutting down the test-instance" );

        if ( instance != null && !instance.node.isClosed() )
        {
            instance.node.stop();
            instance.node.close();
            instance = null;
            Thread.sleep( 1000 );
        }
    }

    private void start()
        throws Exception
    {
        final Settings settings =
            // ImmutableSettings.settingsBuilder().build();
            ImmutableSettings.settingsBuilder().put( GATEWAY_SETTING_KEY, GATEWAY_NO_PERSISTENCE ).build();

        node = NodeBuilder.nodeBuilder().client( false ).local( true ).data( true ).settings( settings ).build();
        node.start();

        // Let the node start goddamnit!
        Thread.sleep( 1000 );

        client = node.client();
    }

    public final static Settings createNodeSettings( File storageDir )
    {

        return ImmutableSettings.settingsBuilder()
            .put( "path.log", new File( storageDir, "log" ).getAbsolutePath() )
            .put( "path.data", new File( storageDir, "data" ).getAbsolutePath() )
            .put( "path.config", "thisisconfigdir" )
            .build();
    }


    public void deleteIndex()
        throws Exception
    {
        try
        {
            getIndexStatus( INDEX_NAME );
            deleteIndex( INDEX_NAME );

            // Let it delete it properly
            Thread.sleep( 1000 );
        }
        catch ( Exception expectedExceptionItsOk )
        {
        }

    }

    private void getIndexStatus( String indexName )
        throws Exception
    {
        this.client.admin().indices().status( new IndicesStatusRequest( indexName ) ).actionGet();
    }

    public PutMappingResponse applyMapping( String indexName, String indexType, String mapping )
        throws Exception
    {
        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType ).source( mapping );

        return this.client.admin().indices().putMapping( mappingRequest ).actionGet();
    }

    public CreateIndexResponse createIndex( String indexName )
        throws Exception
    {
        return this.client.admin().indices().create( new CreateIndexRequest( indexName ) ).actionGet();
    }

    public DeleteIndexResponse deleteIndex( String indexName )
        throws Exception
    {
        return this.client.admin().indices().delete( new DeleteIndexRequest( indexName ) ).actionGet();
    }

    private RefreshResponse refreshIndex( String indexName )
        throws Exception
    {
        return this.client.admin().indices().refresh( new RefreshRequest( indexName ) ).actionGet();
    }


}
