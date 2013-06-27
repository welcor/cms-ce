/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work.quartz;

import java.sql.Connection;

import org.quartz.SchedulerConfigException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.DBConnectionManager;
import org.springframework.jdbc.support.JdbcUtils;

import com.enonic.cms.store.support.ConnectionFactory;

/**
 * This class implements the job store.
 */
public final class QuartzJobStore
    extends JobStoreTX
{
    /**
     * Data source prefix.
     */
    private static final String DATA_SOURCE_PREFIX = "quartzDataSource.";

    /**
     * Initialize the store.
     */
    public void initialize( ClassLoadHelper loadHelper, SchedulerSignaler signaler )
        throws SchedulerConfigException
    {
        final ConnectionFactory factory = QuartzHelper.getConnectionFactory();
        if ( factory == null )
        {
            throw new SchedulerConfigException( "No local ConnectionFactory found for configuration" );
        }

        setDataSource( DATA_SOURCE_PREFIX + getInstanceName() );
        DBConnectionManager.getInstance().addConnectionProvider( getDataSource(), new SimpleConnectionProvider( factory ) );
        super.initialize( loadHelper, signaler );
    }

    /**
     * Close the connection.
     */
    protected void closeConnection( Connection conn )
    {
        JdbcUtils.closeConnection( conn );
    }
}
