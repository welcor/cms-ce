/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.tools.index.ReindexContentToolServiceImpl;

@Component
public class IndexInitializerService
{
    private static final Logger LOG = LoggerFactory.getLogger( IndexInitializerService.class );

    @Value("${cms.index.indexOnStartup}")
    private boolean indexOnStartup;

    @Autowired
    private ReindexContentToolServiceImpl reindexContentToolService;

    @Autowired
    private ElasticSearchIndexService elasticSearchIndexService;

    @Autowired
    private TaskExecutor taskExecutor;

    @PostConstruct
    public void checkForIndexExists()
    {
        if ( !indexOnStartup )
        {
            return;
        }

        final ClusterHealthResponse clusterHealth =
            elasticSearchIndexService.getClusterHealth( ContentIndexServiceImpl.CONTENT_INDEX_NAME, true );

        if ( clusterHealth.isTimedOut() || ClusterHealthStatus.RED.equals( clusterHealth.getStatus() ) )
        {
            LOG.warn( "Not able to get a valid cluster status, skipping reindex" );
        }

        final boolean contentIndexExists = elasticSearchIndexService.indexExists( ContentIndexServiceImpl.CONTENT_INDEX_NAME );

        long count = 0;

        if ( contentIndexExists )
        {
            count = elasticSearchIndexService.count( ContentIndexServiceImpl.CONTENT_INDEX_NAME, IndexType.Content.toString() );
        }

        if ( !contentIndexExists || count == 0 )
        {
            LOG.info( "Index has no data, reindex all content" );

            ReindexJob reindexJob = new ReindexJob();

            taskExecutor.execute( reindexJob );
        }
        else
        {
            LOG.info( "Index exists and has data, continue" );
        }
    }

    private class ReindexJob
        implements Runnable
    {
        final List<String> logEntries = new LinkedList<String>();

        @Override
        public void run()
        {
            if ( !reindexContentToolService.isReIndexInProgress() )
            {
                reindexContentToolService.reindexAllContent( logEntries );
            }
            else
            {
                LOG.info( "Reindex already in content" );
            }
        }

        public List<String> getLogEntries()
        {
            return logEntries;
        }
    }
}
