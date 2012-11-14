/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import com.enonic.cms.framework.jdbc.wrapper.ConnectionWrapper;
import com.enonic.cms.framework.jdbc.wrapper.PreparedStatementWrapper;
import com.enonic.cms.framework.jdbc.wrapper.ResultSetWrapper;
import com.enonic.cms.framework.jdbc.wrapper.StatementWrapper;
import com.enonic.cms.framework.jdbc.dialect.Dialect;

/**
 * This class implements the dialect connection decorator.
 */
public final class DialectConnectionDecorator
    implements ConnectionDecorator
{
    private final Dialect dialect;

    public DialectConnectionDecorator( final Dialect dialect )
    {
        this.dialect = dialect;
    }

    @Override
    public Connection decorate( Connection connection )
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

        @Override
        public PreparedStatement prepareStatement( String sql )
            throws SQLException
        {
            return super.prepareStatement( dialect.translateStatement( sql ) );
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
        protected ResultSet createWrappedResultSet( final ResultSet rs )
        {
            return new ResultSetImpl( rs, this );
        }

        @Override
        public int executeUpdate( final String sql )
            throws SQLException
        {
            return super.executeUpdate( dialect.translateStatement( sql ) );
        }

        @Override
        public boolean execute( final String sql )
            throws SQLException
        {
            return super.execute( dialect.translateStatement( sql ) );
        }

        @Override
        public ResultSet executeQuery( final String sql )
            throws SQLException
        {
            return super.executeQuery( dialect.translateStatement( sql ) );
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
        protected ResultSet createWrappedResultSet( final ResultSet rs )
        {
            return new ResultSetImpl( rs, this );
        }

        @Override
        public int executeUpdate( final String sql )
            throws SQLException
        {
            return super.executeUpdate( dialect.translateStatement( sql ) );
        }

        @Override
        public boolean execute( final String sql )
            throws SQLException
        {
            return super.execute( dialect.translateStatement( sql ) );
        }

        @Override
        public ResultSet executeQuery( final String sql )
            throws SQLException
        {
            return super.executeQuery( dialect.translateStatement( sql ) );
        }

        @Override
        public void setByte( int parameterIndex, byte x )
            throws SQLException
        {
            dialect.setByte( this.stmt, parameterIndex, x );
        }

        @Override
        public void setDouble( int parameterIndex, double x )
            throws SQLException
        {
            dialect.setDouble( this.stmt, parameterIndex, x );
        }

        @Override
        public void setFloat( int parameterIndex, float x )
            throws SQLException
        {
            dialect.setFloat( this.stmt, parameterIndex, x );
        }

        @Override
        public void setInt( int parameterIndex, int x )
            throws SQLException
        {
            dialect.setInt( this.stmt, parameterIndex, x );
        }

        @Override
        public void setNull( int parameterIndex, int sqlType )
            throws SQLException
        {
            dialect.setObject( this.stmt, parameterIndex, null, sqlType );
        }

        @Override
        public void setLong( int parameterIndex, long x )
            throws SQLException
        {
            dialect.setLong( this.stmt, parameterIndex, x );
        }

        @Override
        public void setShort( int parameterIndex, short x )
            throws SQLException
        {
            dialect.setShort( this.stmt, parameterIndex, x );
        }

        @Override
        public void setBoolean( int parameterIndex, boolean x )
            throws SQLException
        {
            dialect.setBoolean( this.stmt, parameterIndex, x );
        }

        @Override
        public void setBytes( int parameterIndex, byte x[] )
            throws SQLException
        {
            dialect.setBytes( this.stmt, parameterIndex, x );
        }

        @Override
        public void setObject( int parameterIndex, Object x )
            throws SQLException
        {
            dialect.setObject( this.stmt, parameterIndex, x );
        }

        @Override
        public void setObject( int parameterIndex, Object x, int targetSqlType )
            throws SQLException
        {
            dialect.setObject( this.stmt, parameterIndex, x, targetSqlType );
        }

        @Override
        public void setString( int parameterIndex, String x )
            throws SQLException
        {
            dialect.setString( this.stmt, parameterIndex, x );
        }

        @Override
        public void setBigDecimal( int parameterIndex, BigDecimal x )
            throws SQLException
        {
            dialect.setBigDecimal( this.stmt, parameterIndex, x );
        }

        @Override
        public void setDate( int parameterIndex, Date x )
            throws SQLException
        {
            dialect.setDate( this.stmt, parameterIndex, x );
        }
    }

    private final class ResultSetImpl
        extends ResultSetWrapper
    {
        public ResultSetImpl( final ResultSet rs, final Statement stmt )
        {
            super( rs, stmt );
        }

        @Override
        public byte getByte( int columnIndex )
            throws SQLException
        {
            return dialect.getByte( this.result, columnIndex );
        }

        @Override
        public double getDouble( int columnIndex )
            throws SQLException
        {
            return dialect.getDouble( this.result, columnIndex );
        }

        @Override
        public float getFloat( int columnIndex )
            throws SQLException
        {
            return dialect.getFloat( this.result, columnIndex );
        }

        @Override
        public int getInt( int columnIndex )
            throws SQLException
        {
            return dialect.getInt( this.result, columnIndex );
        }

        @Override
        public long getLong( int columnIndex )
            throws SQLException
        {
            return dialect.getLong( this.result, columnIndex );
        }

        @Override
        public short getShort( int columnIndex )
            throws SQLException
        {
            return dialect.getShort( this.result, columnIndex );
        }

        @Override
        public boolean getBoolean( int columnIndex )
            throws SQLException
        {
            return dialect.getBoolean( this.result, columnIndex );
        }

        @Override
        public byte[] getBytes( int columnIndex )
            throws SQLException
        {
            return dialect.getBytes( this.result, columnIndex );
        }

        @Override
        public InputStream getBinaryStream( int columnIndex )
            throws SQLException
        {
            return dialect.getBinaryStream( this.result, columnIndex );
        }

        @Override
        public Object getObject( int columnIndex )
            throws SQLException
        {
            return dialect.getObject( this.result, columnIndex );
        }

        @Override
        public String getString( int columnIndex )
            throws SQLException
        {
            return dialect.getString( this.result, columnIndex );
        }

        @Override
        public BigDecimal getBigDecimal( int columnIndex )
            throws SQLException
        {
            return dialect.getBigDecimal( this.result, columnIndex );
        }

        @Override
        public Date getDate( int columnIndex )
            throws SQLException
        {
            return dialect.getDate( this.result, columnIndex );
        }

        @Override
        public Time getTime( int columnIndex )
            throws SQLException
        {
            return dialect.getTime( this.result, columnIndex );
        }

        @Override
        public Timestamp getTimestamp( int columnIndex )
            throws SQLException
        {
            return dialect.getTimestamp( this.result, columnIndex );
        }

        @Override
        public Blob getBlob( int columnIndex )
            throws SQLException
        {
            return dialect.getBlob( this.result, columnIndex );
        }

        @Override
        public byte getByte( String columnName )
            throws SQLException
        {
            return getByte( findColumn( columnName ) );
        }

        @Override
        public double getDouble( String columnName )
            throws SQLException
        {
            return getDouble( findColumn( columnName ) );
        }

        @Override
        public float getFloat( String columnName )
            throws SQLException
        {
            return getFloat( findColumn( columnName ) );
        }

        @Override
        public int getInt( String columnName )
            throws SQLException
        {
            return getInt( findColumn( columnName ) );
        }

        @Override
        public long getLong( String columnName )
            throws SQLException
        {
            return getLong( findColumn( columnName ) );
        }

        @Override
        public short getShort( String columnName )
            throws SQLException
        {
            return getShort( findColumn( columnName ) );
        }

        @Override
        public boolean getBoolean( String columnName )
            throws SQLException
        {
            return getBoolean( findColumn( columnName ) );
        }

        @Override
        public byte[] getBytes( String columnName )
            throws SQLException
        {
            return getBytes( findColumn( columnName ) );
        }

        @Override
        public InputStream getAsciiStream( String columnName )
            throws SQLException
        {
            return getAsciiStream( findColumn( columnName ) );
        }

        @Override
        public InputStream getBinaryStream( String columnName )
            throws SQLException
        {
            return getBinaryStream( findColumn( columnName ) );
        }

        @Override
        public Reader getCharacterStream( String columnName )
            throws SQLException
        {
            return getCharacterStream( findColumn( columnName ) );
        }

        @Override
        public Object getObject( String columnName )
            throws SQLException
        {
            return getObject( findColumn( columnName ) );
        }

        @Override
        public String getString( String columnName )
            throws SQLException
        {
            return getString( findColumn( columnName ) );
        }

        @Override
        public BigDecimal getBigDecimal( String columnName )
            throws SQLException
        {
            return getBigDecimal( findColumn( columnName ) );
        }

        @Override
        public Blob getBlob( String columnName )
            throws SQLException
        {
            return getBlob( findColumn( columnName ) );
        }

        @Override
        public Clob getClob( String columnName )
            throws SQLException
        {
            return getClob( findColumn( columnName ) );
        }

        @Override
        public Date getDate( String columnName )
            throws SQLException
        {
            return getDate( findColumn( columnName ) );
        }

        @Override
        public Time getTime( String columnName )
            throws SQLException
        {
            return getTime( findColumn( columnName ) );
        }

        @Override
        public Timestamp getTimestamp( String columnName )
            throws SQLException
        {
            return getTimestamp( findColumn( columnName ) );
        }
    }
}
