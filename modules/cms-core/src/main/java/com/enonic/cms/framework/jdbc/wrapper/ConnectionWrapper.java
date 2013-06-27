/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.wrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.openjpa.lib.jdbc.DelegatingConnection;

/**
 * This class implements a delegating connection. All connections that will delegate to a source connection will typically extend this
 * class.
 */
public abstract class ConnectionWrapper
    extends DelegatingConnection
{
    private final Connection conn;

    public ConnectionWrapper( final Connection conn )
    {
        super( conn );
        this.conn = conn;
    }

    @Override
    protected Statement createStatement( final boolean wrap )
        throws SQLException
    {
        final Statement stmt = this.conn.createStatement();
        return wrapStatement( stmt, wrap );
    }

    @Override
    protected Statement createStatement( final int type, final int concur, final boolean wrap )
        throws SQLException
    {
        final Statement stmt = this.conn.createStatement( type, concur );
        return wrapStatement( stmt, wrap );
    }

    @Override
    protected Statement createStatement( final int type, final int concur, final int holdability, final boolean wrap )
        throws SQLException
    {
        final Statement stmt = this.conn.createStatement( type, concur, holdability );
        return wrapStatement( stmt, wrap );
    }

    @Override
    protected PreparedStatement prepareStatement( final String sql, final boolean wrap )
        throws SQLException
    {
        final PreparedStatement stmt = this.conn.prepareStatement( sql );
        return wrapPreparedStatement( stmt, sql, wrap );
    }

    @Override
    protected PreparedStatement prepareStatement( final String sql, final int type, final int concur, final boolean wrap )
        throws SQLException
    {
        final PreparedStatement stmt = this.conn.prepareStatement( sql, type, concur );
        return wrapPreparedStatement( stmt, sql, wrap );
    }

    @Override
    protected PreparedStatement prepareStatement( final String sql, final int type, final int concur, final int holdability,
                                                  final boolean wrap )
        throws SQLException
    {
        final PreparedStatement stmt = this.conn.prepareStatement( sql, type, concur, holdability );
        return wrapPreparedStatement( stmt, sql, wrap );
    }

    private Statement wrapStatement( final Statement stmt, final boolean wrap )
        throws SQLException
    {
        return wrap ? createWrappedStatement( stmt ) : stmt;
    }

    private PreparedStatement wrapPreparedStatement( final PreparedStatement stmt, final String sql, final boolean wrap )
        throws SQLException
    {
        return wrap ? createWrappedPreparedStatement( stmt, sql ) : stmt;
    }

    protected abstract Statement createWrappedStatement( final Statement stmt )
        throws SQLException;

    protected abstract PreparedStatement createWrappedPreparedStatement( final PreparedStatement stmt, final String sql )
        throws SQLException;
}
