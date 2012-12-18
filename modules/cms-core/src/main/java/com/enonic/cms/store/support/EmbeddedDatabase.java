package com.enonic.cms.store.support;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;

final class EmbeddedDatabase
{
    private String jdbcUrl;

    private String userName;

    private String password;

    private JdbcConnectionPool pool;

    private int maxConnections;

    public void setJdbcUrl( final String jdbcUrl )
    {
        this.jdbcUrl = jdbcUrl;
    }

    public void setUserName( final String userName )
    {
        this.userName = userName;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setMaxConnections( final int maxConnections )
    {
        this.maxConnections = maxConnections;
    }

    public void initialize()
    {
        this.pool = JdbcConnectionPool.create( this.jdbcUrl, this.userName, this.password );
        this.pool.setMaxConnections( this.maxConnections );
    }

    public void dispose()
    {
        if (this.pool != null) {
            this.pool.dispose();
        }
    }

    public DataSource getDataSource()
    {
        return this.pool;
    }
}
