/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.jdbc.wrapper.ConnectionWrapper;
import com.enonic.cms.framework.jdbc.wrapper.PreparedStatementWrapper;
import com.enonic.cms.framework.jdbc.wrapper.StatementWrapper;

/**
 * This class implements the logging connection decorator.
 */
public final class LoggingConnectionDecorator
    implements ConnectionDecorator
{
    private static final Logger LOG = LoggerFactory.getLogger( LoggingConnectionDecorator.class.getName() );

    @Override
    public Connection decorate( final Connection connection )
        throws SQLException
    {
        return new ConnectionImpl( connection );
    }

    private void logSql( final String sql )
        throws SQLException
    {
        LOG.info( "JdbcSql: {}", sql );
    }

    private final class ConnectionImpl
        extends ConnectionWrapper
    {
        public ConnectionImpl( final Connection conn )
        {
            super( conn );
        }

        @Override
        protected Statement createWrappedStatement( final Statement stmt )
        {
            return new StatementImpl( stmt, this );
        }

        @Override
        protected PreparedStatement createWrappedPreparedStatement( final PreparedStatement stmt, final String sql )
            throws SQLException
        {
            return new PreparedStatementImpl( stmt, this );
        }
    }

    private final class StatementImpl
        extends StatementWrapper
    {
        public StatementImpl( final Statement stmt, final Connection conn )
        {
            super( stmt, conn );
        }

        @Override
        public int executeUpdate( final String sql )
            throws SQLException
        {
            logSql( sql );
            return super.executeUpdate( sql );
        }

        @Override
        public boolean execute( final String sql )
            throws SQLException
        {
            logSql( sql );
            return super.execute( sql );
        }

        @Override
        public ResultSet executeQuery( final String sql )
            throws SQLException
        {
            logSql( sql );
            return super.executeQuery( sql );
        }

        @Override
        protected ResultSet createWrappedResultSet( final ResultSet result )
            throws SQLException
        {
            return result;
        }
    }

    private final class PreparedStatementImpl
        extends PreparedStatementWrapper
    {
        public PreparedStatementImpl( final PreparedStatement stmt, final Connection conn )
        {
            super( stmt, conn );
        }

        @Override
        public int executeUpdate( final String sql )
            throws SQLException
        {
            logSql(sql );
            return super.executeUpdate( sql );
        }

        @Override
        public boolean execute( final String sql )
            throws SQLException
        {
            logSql( sql );
            return super.execute( sql );
        }

        @Override
        public ResultSet executeQuery( final String sql )
            throws SQLException
        {
            logSql( sql );
            return super.executeQuery( sql );
        }

        @Override
        protected ResultSet createWrappedResultSet( final ResultSet result )
            throws SQLException
        {
            return result;
        }
    }
}
