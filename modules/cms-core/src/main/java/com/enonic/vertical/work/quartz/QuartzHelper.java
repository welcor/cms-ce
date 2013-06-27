/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work.quartz;

import javax.sql.DataSource;

import com.enonic.vertical.work.WorkRunner;

import com.enonic.cms.store.support.ConnectionFactory;

/**
 * This holds the data source in current thread for quartz configuration purposes.
 */
public final class QuartzHelper
{
    private static ConnectionFactory CONNECTION_FACTORY;

    private static WorkRunner WORK_RUNNER;

    public static void setConnectionFactory( ConnectionFactory factory )
    {
        CONNECTION_FACTORY = factory;
    }

    public static ConnectionFactory getConnectionFactory()
    {
        return CONNECTION_FACTORY;
    }

    public static void setWorkRunner( WorkRunner workRunner )
    {
        WORK_RUNNER = workRunner;
    }

    public static WorkRunner getWorkRunner()
    {
        return WORK_RUNNER;
    }
}
