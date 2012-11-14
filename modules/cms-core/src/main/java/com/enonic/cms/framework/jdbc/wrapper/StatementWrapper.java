/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.wrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.openjpa.lib.jdbc.DelegatingStatement;

/**
 * This class implements the delegating statement.
 */
public abstract class StatementWrapper
    extends DelegatingStatement
{
    protected final Statement stmt;

    public StatementWrapper( final Statement stmt, final Connection conn )
    {
        super( stmt, conn );
        this.stmt = stmt;
    }

    @Override
    protected ResultSet executeQuery( final String sql, final boolean wrap )
        throws SQLException
    {
        final ResultSet result = this.stmt.executeQuery( sql );
        return wrapResultSet( result, wrap );
    }

    @Override
    protected ResultSet getResultSet( final boolean wrap )
        throws SQLException
    {
        final ResultSet result = this.stmt.getResultSet();
        return wrapResultSet( result, wrap );
    }

    private ResultSet wrapResultSet( final ResultSet result, final boolean wrap )
        throws SQLException
    {
        return wrap ? createWrappedResultSet( result ) : result;
    }

    protected abstract ResultSet createWrappedResultSet( final ResultSet result )
        throws SQLException;
}
