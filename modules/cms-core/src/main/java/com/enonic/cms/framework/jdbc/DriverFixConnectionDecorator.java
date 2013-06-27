/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.framework.jdbc;

import com.enonic.cms.framework.jdbc.wrapper.ConnectionWrapper;
import com.enonic.cms.framework.jdbc.wrapper.PreparedStatementWrapper;
import com.enonic.cms.framework.jdbc.wrapper.StatementWrapper;

import java.sql.*;

/**
 * This class fixes some annoying things in jdbc drivers. At this point, only setQueryTimeout is fixed.
 * Reason: PostgreSQL JDBC driver versions 8.3, 8.4, 9.0 do not implement <code>setQueryTimeout(int)</code> method.
 */
public final class DriverFixConnectionDecorator
    implements ConnectionDecorator
{
    @Override
    public Connection decorate( final Connection connection )
        throws SQLException
    {
        return new ConnectionImpl( connection );
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
        public void setQueryTimeout( final int seconds )
            throws SQLException
        {
            doSetQueryTimeout( this.stmt, seconds );
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
        public void setQueryTimeout( int seconds )
            throws SQLException
        {
            doSetQueryTimeout( this.stmt, seconds );
        }

        @Override
        protected ResultSet createWrappedResultSet( final ResultSet result )
            throws SQLException
        {
            return result;
        }
    }

    private void doSetQueryTimeout( final Statement stmt, final int seconds )
        throws SQLException
    {
        try {
            stmt.setQueryTimeout( seconds );
        } catch ( final SQLException e ) {
            // Ignore
        }
    }
}
