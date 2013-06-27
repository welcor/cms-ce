/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.enonic.cms.core.plugin.PluginManager;

public class WorkRunnerImpl
    implements WorkRunner
{
    private PluginManager pluginManager;

    private TransactionTemplate transactionTemplate;

    public void executeWork( final String className, final Properties props )
        throws Exception
    {
        executeInTx( className, props );
    }

    private void executeInTx( final String className, final Properties props )
        throws Exception
    {
        Exception error = (Exception) transactionTemplate.execute( new TransactionCallback()
        {
            public Object doInTransaction( TransactionStatus status )
            {
                try
                {
                    WorkHelper.executeWork( pluginManager.getExtensions(), className, props );
                    return null;
                }
                catch ( Exception e )
                {
                    status.setRollbackOnly();
                    return e;
                }
            }
        } );

        if ( error != null )
        {
            throw error;
        }
    }

    public void setTransactionTemplate( TransactionTemplate transactionTemplate )
    {
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public void setPluginManager( final PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }
}
