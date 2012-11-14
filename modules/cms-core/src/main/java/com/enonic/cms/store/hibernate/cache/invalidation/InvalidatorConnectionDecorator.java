/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.enonic.cms.framework.jdbc.ConnectionDecorator;
import com.enonic.cms.framework.jdbc.wrapper.ConnectionWrapper;
import com.enonic.cms.framework.jdbc.wrapper.PreparedStatementWrapper;
import com.enonic.cms.framework.jdbc.wrapper.StatementWrapper;

/**
 * This class implements a connection decorator based on the auto cache invalidator.
 */
public final class InvalidatorConnectionDecorator
    implements ConnectionDecorator
{
    private final CacheInvalidator invalidator;

    public InvalidatorConnectionDecorator( final CacheInvalidator invalidator )
    {
        this.invalidator = invalidator;
    }

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
            return new PreparedStatementImpl( sql, stmt, this );
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
            invalidator.invalidateSql( sql );
            return super.executeUpdate( sql );
        }

        @Override
        public boolean execute( final String sql )
            throws SQLException
        {
            invalidator.invalidateSql( sql );
            return super.execute( sql );
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
        private final String sql;

        private final ArrayList<Object> paramList;

        public PreparedStatementImpl( final String sql, final PreparedStatement stmt, final Connection conn )
        {
            super( stmt, conn );
            this.sql = sql;
            this.paramList = new ArrayList<Object>();
        }

        @Override
        public boolean execute()
            throws SQLException
        {
            invalidator.invalidateSql( this.sql, this.paramList );
            return super.execute();
        }

        @Override
        public int executeUpdate()
            throws SQLException
        {
            invalidator.invalidateSql( this.sql, this.paramList );
            return super.executeUpdate();
        }

        @Override
        public int executeUpdate( String sql )
            throws SQLException
        {
            invalidator.invalidateSql( sql );
            return super.executeUpdate( sql );
        }

        @Override
        public boolean execute( String sql )
            throws SQLException
        {
            invalidator.invalidateSql( sql );
            return super.execute( sql );
        }

        @Override
        public void setObject( int parameterIndex, Object x )
            throws SQLException
        {
            super.setObject( parameterIndex, x );
            setParam( parameterIndex, x );
        }

        @Override
        public void setObject( int parameterIndex, Object x, int targetSqlType )
            throws SQLException
        {
            super.setObject( parameterIndex, x, targetSqlType );
            setParam( parameterIndex, x );
        }

        @Override
        public void setObject( int parameterIndex, Object x, int targetSqlType, int scale )
            throws SQLException
        {
            super.setObject( parameterIndex, x, targetSqlType, scale );
            setParam( parameterIndex, x );
        }

        @Override
        public void setString( int parameterIndex, String x )
            throws SQLException
        {
            super.setString( parameterIndex, x );
            setParam( parameterIndex, x );
        }

        @Override
        public void setInt( int parameterIndex, int x )
            throws SQLException
        {
            super.setInt( parameterIndex, x );
            setParam( parameterIndex, x );
        }

        private void setParam( int index, Object value )
        {
            while ( this.paramList.size() < index )
            {
                this.paramList.add( null );
            }

            this.paramList.set( index - 1, value );
        }

        @Override
        protected ResultSet createWrappedResultSet( final ResultSet result )
            throws SQLException
        {
            return result;
        }
    }
}
