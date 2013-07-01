/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.vacuum;

import java.sql.Connection;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.engine.PresentationEngine;

import com.enonic.cms.framework.blob.gc.GarbageCollector;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

@Component
public final class VacuumServiceImpl
    implements VacuumService
{
    private final static String VACUUM_READ_LOGS_SQL = "DELETE FROM tLogEntry WHERE len_lTypeKey = 7";

    @Autowired
    private GarbageCollector garbageCollector;

    @Autowired
    protected PresentationEngine baseEngine;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    private MemberOfResolver memberOfResolver;


    private ProgressInfo progressInfo = new ProgressInfo();

    /**
     * Clean read logs.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cleanReadLogs()
    {
        if ( !isAdmin() )
        {
            return;
        }

        try
        {
            final Connection conn = baseEngine.getConnection();

            vacuumReadLogs( conn );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Failed to clean read logs", e );
        }
    }

    /**
     * Clean unused content.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cleanUnusedContent()
    {
        if ( progressInfo.isInProgress() || !isAdmin() )
        {
            return;
        }

        progressInfo.setInProgress( true );
        progressInfo.setLogLine( "Cleaning unused content..." );
        progressInfo.setPercent( 0 );

        try
        {
            final Connection conn = baseEngine.getConnection();

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
        catch ( final Exception e )
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

    public ProgressInfo getProgressInfo()
    {
        if ( !isAdmin() )
        {
            return ProgressInfo.NONE;
        }

        return progressInfo;
    }

    private boolean isAdmin()
    {
        final User user = securityService.getLoggedInAdminConsoleUser();
        return memberOfResolver.hasEnterpriseAdminPowers( user.getKey() );
    }


    /**
     * Vacuum binaries.
     */
    private void vacuumBinaries( final Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_BINARIES_STATEMENTS );
    }

    /**
     * Vacuum contents.
     */
    private void vacuumContents( final Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_CONTENT_STATEMENTS );
    }

    /**
     * Vacuum categories.
     */
    private void vacuumCategories( final Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_CATEGORIES_STATEMENTS );
    }

    /**
     * Vacuum arvhives.
     */
    private void vacuumArchives( final Connection conn )
        throws Exception
    {
        executeStatements( conn, VacuumContentSQL.VACUUM_ARCHIVES_STATEMENTS );
    }

    /**
     * Vacuum read logs.
     */
    private void vacuumReadLogs( final Connection conn )
        throws Exception
    {
        executeStatements( conn, new String[]{VACUUM_READ_LOGS_SQL} );
    }

    private void vacuumBlobStore()
    {
        this.garbageCollector.process();
    }

    /**
     * Execute a list of statements.
     */
    private void executeStatements( final Connection conn, final String[] sqlList )
        throws Exception
    {
        for ( final String sql : sqlList )
        {
            executeStatement( conn, sql );
        }
    }

    /**
     * Execute statement.
     */
    private void executeStatement( final Connection conn, final String sql )
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
            baseEngine.close( stmt );
        }
    }
}
