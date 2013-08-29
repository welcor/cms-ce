/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work;

import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.enonic.cms.core.plugin.ext.TaskHandlerExtensions;
import com.enonic.cms.store.support.FlexibleAnnotationTransactionAttributeSource;

@Component
public class WorkRunnerImpl
    implements WorkRunner
{
    @Autowired
    private TaskHandlerExtensions extensions;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Value("${cms.scheduler.tx.timeout}")
    private int extendedTimeout;

    private FlexibleAnnotationTransactionAttributeSource flexibleAnnotationTransactionAttributeSource;

    @PostConstruct
    public void initService()
    {
        final TransactionAttributeSource transactionAttributeSource = transactionInterceptor.getTransactionAttributeSource();

        flexibleAnnotationTransactionAttributeSource =
            new FlexibleAnnotationTransactionAttributeSource( transactionAttributeSource, extendedTimeout );

        transactionInterceptor.setTransactionAttributeSource( flexibleAnnotationTransactionAttributeSource );
    }

    public void executeWork( final String className, final Properties props )
        throws Exception
    {
        flexibleAnnotationTransactionAttributeSource.extendTransactionTimeoutForThisThread();
        executeInTx( className, props );
    }

    private void executeInTx( final String className, final Properties props )
        throws Exception
    {
        final HibernateTemplate template = new HibernateTemplate( sessionFactory );

        final Exception exception = template.execute( new HibernateCallback<Exception>()
        {
            @Override
            public Exception doInHibernate( final Session session )
                throws HibernateException, SQLException
            {
                try
                {
                    WorkHelper.executeWork( extensions, className, props );
                    session.flush();
                    return null;
                }
                catch ( final Exception e )
                {
                    return e;
                }
            }
        } );

        if ( exception != null )
        {
            throw exception;
        }
    }
}
