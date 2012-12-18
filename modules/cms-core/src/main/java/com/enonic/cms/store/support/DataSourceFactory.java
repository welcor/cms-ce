/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import com.enonic.cms.framework.jdbc.DecoratedDataSource;
import com.enonic.cms.framework.jdbc.DriverFixConnectionDecorator;

public final class DataSourceFactory
    implements FactoryBean<DataSource>, InitializingBean, DisposableBean
{
    private DataSource dataSource;

    private String jndiName;

    private EmbeddedDatabase embeddedDatabase;

    private boolean useEmbedded;

    public DataSourceFactory()
    {
        this.embeddedDatabase = new EmbeddedDatabase();
    }

    public void setJndiName( final String jndiName )
    {
        this.jndiName = jndiName;
    }

    public void setUserName( final String userName )
    {
        this.embeddedDatabase.setUserName( userName );
    }

    public void setPassword( final String password )
    {
        this.embeddedDatabase.setPassword( password );
    }

    public void setJdbcUrl( final String jdbcUrl )
    {
        this.embeddedDatabase.setJdbcUrl( jdbcUrl );
    }

    public void setMaxConnections( final int maxConnections )
    {
        this.embeddedDatabase.setMaxConnections( maxConnections );
    }

    public void setUseEmbedded( final boolean useEmbedded )
    {
        this.useEmbedded = useEmbedded;
    }

    public void afterPropertiesSet()
    {
        final DataSource original = createDataSource();
        this.dataSource = new DecoratedDataSource( original, new DriverFixConnectionDecorator() );
    }

    private DataSource createDataSource()
    {
        if ( this.useEmbedded )
        {
            return createEmbeddedDatabase();
        }
        else
        {
            return lookupUsingJndi();
        }
    }

    private DataSource lookupUsingJndi()
    {
        final JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource( this.jndiName );
    }

    private DataSource createEmbeddedDatabase()
    {
        this.embeddedDatabase.initialize();
        return this.embeddedDatabase.getDataSource();
    }

    public DataSource getObject()
    {
        return this.dataSource;
    }

    public Class getObjectType()
    {
        return DataSource.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    @Override
    public void destroy()
    {
        this.embeddedDatabase.dispose();
    }
}
