/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.store.support;

import java.lang.reflect.Method;

import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

/**
 * overrides transaction timeouts inside spring transaction manager
 */
public class FlexibleAnnotationTransactionAttributeSource
    implements TransactionAttributeSource
{
    private TransactionAttributeSource annotationTransactionAttributeSource;

    private int extendedTimeout;

    private ThreadLocal<Boolean> longTransaction = new ThreadLocal<Boolean>();

    public FlexibleAnnotationTransactionAttributeSource( final TransactionAttributeSource annotationTransactionAttributeSource,
                                                         final int extendedTimeout )
    {
        this.annotationTransactionAttributeSource = annotationTransactionAttributeSource;
        this.extendedTimeout = extendedTimeout;

        longTransaction.set( false );
    }

    public void extendTransactionTimeoutForThisThread()
    {
        this.longTransaction.set( true );
    }

    @Override
    public TransactionAttribute getTransactionAttribute( final Method method, final Class<?> targetClass )
    {
        final TransactionAttribute transactionAttribute =
            annotationTransactionAttributeSource.getTransactionAttribute( method, targetClass );

        return new FlexibleTransactionAttribute( transactionAttribute, longTransaction, extendedTimeout );
    }

}
