/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.instanttrace;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class InstantTraceId
{
    private Long traceCompletedNumber;

    public InstantTraceId( final String traceCompletedNumber )
    {
        this.traceCompletedNumber = new Long( traceCompletedNumber );
    }

    public InstantTraceId( final Long traceCompletedNumber )
    {
        Preconditions.checkNotNull( traceCompletedNumber );
        this.traceCompletedNumber = traceCompletedNumber;
    }

    public Long getTraceCompletedNumber()
    {
        return traceCompletedNumber;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final InstantTraceId that = (InstantTraceId) o;

        return Objects.equal( traceCompletedNumber, that.traceCompletedNumber );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( traceCompletedNumber );
    }

    @Override
    public String toString()
    {
        return traceCompletedNumber.toString();
    }
}
