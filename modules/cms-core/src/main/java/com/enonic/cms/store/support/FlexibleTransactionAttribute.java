/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.store.support;

import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * overrides transaction timeouts inside spring transaction manager
 */
public class FlexibleTransactionAttribute
    implements TransactionAttribute
{
    private final TransactionAttribute transactionAttribute;

    private ThreadLocal<Boolean> longTransaction;

    private int extendedTimeout;

    public FlexibleTransactionAttribute( final TransactionAttribute transactionAttribute, final ThreadLocal<Boolean> longTransaction,
                                         final int extendedTimeout )
    {
        this.transactionAttribute = transactionAttribute;
        this.longTransaction = longTransaction;
        this.extendedTimeout = extendedTimeout;
    }

    @Override
    public String getQualifier()
    {
        return transactionAttribute.getQualifier();
    }

    @Override
    public boolean rollbackOn( final Throwable ex )
    {
        return transactionAttribute.rollbackOn( ex );
    }

    @Override
    public int getPropagationBehavior()
    {
        return transactionAttribute.getPropagationBehavior();
    }

    @Override
    public int getIsolationLevel()
    {
        return transactionAttribute.getIsolationLevel();
    }

    @Override
    public int getTimeout()
    {
        final int timeout = transactionAttribute.getTimeout();

        // do not decrease timeout
        if ( timeout >= extendedTimeout )
        {
            return timeout;
        }

        final boolean isLongTransaction = longTransaction.get() != null && longTransaction.get();
        return isLongTransaction ? extendedTimeout : timeout;
    }

    @Override
    public boolean isReadOnly()
    {
        return transactionAttribute.isReadOnly();
    }

    @Override
    public String getName()
    {
        return transactionAttribute.getName();
    }
}
