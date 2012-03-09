package com.enonic.vertical.work.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.utils.ConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.enonic.cms.store.support.ConnectionFactory;

/**
 * Connection provider that delegates to data source.
 */
public final class SimpleConnectionProvider
    implements ConnectionProvider
{
    /**
     * Data source.
     */
    private final ConnectionFactory factory;

    /**
     * Construct the provider.
     */
    public SimpleConnectionProvider( ConnectionFactory factory )
    {
        this.factory = factory;
    }

    /**
     * Return the connection.
     */
    public Connection getConnection()
        throws SQLException
    {
        return this.factory.getConnection( true );
    }

    /**
     * Shutdown the provider.
     */
    public void shutdown()
        throws SQLException
    {
        // Do nothing
    }
}
