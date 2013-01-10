/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.core.tools.AbstractToolController;

public class ReindexContentToolController
    extends AbstractToolController
{
    private ReindexContentToolService reindexContentToolService;

    private List<String> logEntries = new ArrayList<String>();

    private ElasticSearchIndexService elasticSearchIndexService;

    private ContentIndexService contentIndexService;

    private ObjectMapper jacksonObjectMapper;

    public ReindexContentToolController()
    {
        jacksonObjectMapper = new ObjectMapper().configure( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false );
    }

    public String generate( final ProgressInfo trace )
    {
        try
        {
            return jacksonObjectMapper.writeValueAsString( trace );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to transform objects to JSON: " + e.getMessage(), e );
        }
    }

    @Override
    protected void doPost( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        doGet( req, res );
    }


    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final Map<String, Object> model = new HashMap<String, Object>();

        if ( req.getParameter( "reindex" ) != null )
        {
            if ( req.getParameter( "recreateIndex" ) != null )
            {
                recreateIndex();
            }

            startReindexAllContentTypes();
            redirectToReferrer( req, res );
        }
        else if ( "progress".equals( req.getParameter( "info" ) ) )
        {
            final ProgressInfo progressInfo = reindexContentToolService.getProgressInfo();

            progressInfo.setInProgress( reindexContentToolService.isReIndexInProgress() );

            res.setHeader( "Content-Type", "application/json; charset=UTF-8" );
            res.getWriter().println( generate( progressInfo ) );

            return;
        }

        final String page = "logLines".equals( req.getParameter( "info" ) ) ? "reindexLogPage" : "reindexContentPage";

        model.put( "reindexInProgress", reindexContentToolService.isReIndexInProgress() );
        model.put( "reindexLog", logEntries );
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "back", req.getParameter( "back" ) );

        renderView( req, res, model, page );
    }

    private void recreateIndex()
    {
        if ( !reindexContentToolService.isReIndexInProgress() )
        {
            elasticSearchIndexService.deleteIndex( "cms" );
            contentIndexService.createIndex();
        }
    }


    private synchronized void startReindexAllContentTypes()
    {

        if ( reindexContentToolService.isReIndexInProgress() )
        {
            return;
        }

        reindexContentToolService.setReIndexInProgress( Boolean.TRUE );

        Thread reindexThread = new Thread( new Runnable()
        {
            public void run()
            {

                try
                {
                    reindexContentToolService.reindexAllContent( logEntries );
                }
                finally
                {
                    reindexContentToolService.setReIndexInProgress( Boolean.FALSE );
                }
            }
        }, "Reindex Content Thread" );

        reindexThread.start();
    }

    @Autowired
    public void setReindexContentToolService( ReindexContentToolService value )
    {
        this.reindexContentToolService = value;
    }

    @Autowired
    public void setElasticSearchIndexService( final ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }

    @Autowired
    public void setContentIndexService( final ContentIndexService contentIndexService )
    {
        this.contentIndexService = contentIndexService;
    }
}
