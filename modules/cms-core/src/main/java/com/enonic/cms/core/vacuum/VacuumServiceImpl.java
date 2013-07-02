/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.vacuum;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.blob.gc.GarbageCollector;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.store.support.ConnectionFactory;

@Component
public final class VacuumServiceImpl
    implements VacuumService
{
    private final static String VACUUM_READ_LOGS_SQL = "DELETE FROM tLogEntry WHERE len_lTypeKey = 7";

    @Autowired
    private GarbageCollector garbageCollector;

    @Autowired
    protected ConnectionFactory connectionFactory;

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
        if ( progressInfo.isInProgress() || !isAdmin() )
        {
            return;
        }

        try
        {
            startProgress( "Cleaning read logs..." );

            final Connection conn = connectionFactory.getConnection( true );

            setProgress( "Vacuum read logs...", 5 );

            vacuumReadLogs( conn );
        }
        catch ( final Exception e )
        {
            setProgress( "Failed to clean read logs: " + e.getMessage(), 100 );

            throw new RuntimeException( "Failed to clean read logs", e );
        }
        finally
        {
            finishProgress();
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

        try
        {
            startProgress( "Cleaning unused content..." );

            final Connection conn = connectionFactory.getConnection( true );

            setProgress( "Vacuum binaries...", 5 );
            vacuumBinaries( conn );

            setProgress( "Vacuum contents...", 20 );
            vacuumContents( conn );

            setProgress( "Vacuum categories...", 40 );
            vacuumCategories( conn );

            setProgress( "Vacuum archives...", 60 );
            vacuumArchives( conn );

            setProgress( "Vacuum blob store...", 80 );
            vacuumBlobStore();

        }
        catch ( final Exception e )
        {
            setProgress( "Failed to clean unused content: " + e.getMessage(), 100 );

            throw new RuntimeException( "Failed to clean unused content", e );
        }
        finally
        {
            finishProgress();
        }
    }

    /**
     * returns progress info about either Clean unused content or Clean read logs.
     */
    public ProgressInfo getProgressInfo()
    {
        if ( !isAdmin() )
        {
            return ProgressInfo.NONE;
        }

        return progressInfo;
    }

    private void startProgress( final String logLine )
    {
        setProgress( logLine, 0 );
        progressInfo.setInProgress( true );
    }

    private void setProgress( final String logLine, final int percent )
    {
        progressInfo.setLogLine( logLine );
        progressInfo.setPercent( percent );
    }

    private void finishProgress()
    {
        setProgress( "Finished. Last job was executed at " + new Date().toString(), 100 );
        progressInfo.setInProgress( false );
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
            JdbcUtils.closeStatement( stmt );
        }
    }
}
