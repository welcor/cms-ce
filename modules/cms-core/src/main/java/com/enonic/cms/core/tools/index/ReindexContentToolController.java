/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final HashMap<String, Object> model = new HashMap<String, Object>();

        if ( req.getParameter( "reindex" ) != null )
        {
            if ( req.getParameter( "recreateIndex" ) != null )
            {
                recreateIndex();
            }

            startReindexAllContentTypes();
            redirectToReferrer( req, res );
        }

        model.put( "reindexInProgress", reindexContentToolService.isReIndexInProgress() );
        model.put( "reindexLog", logEntries );
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );

        renderView( req, res, model, "reindexContentPage" );
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
                    reindexContentToolService.setLastReindexFailed( false );
                    reindexContentToolService.reindexAllContent( logEntries );
                }
                catch ( Exception e )
                {
                    reindexContentToolService.setLastReindexFailed( true );
                    throw new ReindexContentException( "Reindex content failed", e );
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
