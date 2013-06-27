/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.blob.gc.GarbageCollector;

import com.enonic.cms.core.tools.index.ProgressInfo;
import com.enonic.cms.store.VacuumContentSQL;

/**
 * This class implements the system handler that takes care of creating database schema and populating version numbers.
 */
@Component
public final class VacuumHandler
    extends BaseHandler
{
    private final static Logger LOG = LoggerFactory.getLogger( VacuumHandler.class );

    /**
     * Delete read logs.
     */
    private final static String VACUUM_READ_LOGS_SQL = "DELETE FROM tLogEntry WHERE len_lTypeKey = 7";

    @Autowired
    private GarbageCollector garbageCollector;

    private ProgressInfo progressInfo = new ProgressInfo();


    /**
     * Clean read logs.
     */
    public void cleanReadLogs()
    {
        try
        {
            Connection conn = getConnection();

            vacuumReadLogs( conn );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to clean read logs", e );
        }
    }

    /**
     * Clean unused content.
     */
    public void cleanUnusedContent()
    {
        if ( progressInfo.isInProgress() )
        {
            return;
        }

        progressInfo.setInProgress( true );
        progressInfo.setLogLine( "Cleaning unused content..." );
        progressInfo.setPercent( 0 );

        try
        {
            final Connection conn = getConnection();

            progressInfo.setLogLine( "Vacuum binaries..." );
            progressInfo.setPercent( 2 );
            vacuumBinaries( conn );

            progressInfo.setLogLine( "Vacuum contents..." );
            progressInfo.setPercent( 20 );
            vacuumContents( conn );

            progressInfo.setLogLine( "Vacuum categories..." );
            progressInfo.setPercent( 40 );
            vacuumCategories( conn );

            progressInfo.setLogLine( "Vacuum archives..." );
            progressInfo.setPercent( 60 );
            vacuumArchives( conn );

            progressInfo.setLogLine( "Vacuum blob store..." );
            progressInfo.setPercent( 80 );
            vacuumBlobStore();
        }
        catch ( Exception e )
        {
            progressInfo.setLogLine( "Failed to clean unused content: " + e.getMessage() );

            throw new RuntimeException( "Failed to clean unused content", e );
        }
        finally
        {
            progressInfo.setLogLine( "Done." );

            progressInfo.setPercent( 100 );
            progressInfo.setInProgress( false );
        }
    }

    /**
     * Vacuum binaries.
     */
    private void vacuumBinaries( Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_BINARIES_STATEMENTS );
    }

    /**
     * Vacuum contents.
     */
    private void vacuumContents( Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_CONTENT_STATEMENTS );
    }

    /**
     * Vacuum categories.
     */
    private void vacuumCategories( Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_CATEGORIES_STATEMENTS );
    }

    /**
     * Vacuum arvhives.
     */
    private void vacuumArchives( Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_ARCHIVES_STATEMENTS );
    }

    /**
     * Vacuum read logs.
     */
    private void vacuumReadLogs( Connection conn )
        throws Exception
    {
        executeStatements( conn, new String[]{VACUUM_READ_LOGS_SQL} );
    }

    /**
     * Execute a list of statements.
     */
    private void executeStatements( Connection conn, String[] sqlList )
        throws Exception
    {
        for ( String sql : sqlList )
        {
            executeStatement( conn, sql );
        }
    }

    /**
     * Execute statement.
     */
    private void executeStatement( Connection conn, String sql )
        throws Exception
    {
        Statement stmt = null;

        try
        {
            stmt = conn.createStatement();
            stmt.execute( sql );
        }
        finally
        {
            close( stmt );
        }
    }

    private void vacuumBlobStore()
    {
        this.garbageCollector.process();
    }

    public ProgressInfo getProgressInfo()
    {
        return progressInfo;
    }
}
