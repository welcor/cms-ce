package com.enonic.cms.web.portal.instanttrace;

import com.enonic.cms.core.security.user.UserKey;

public class InstantTraceId
{
    private UserKey user;

    private Long traceCompletedNumber;

    public InstantTraceId( final String instanceTraceId )
    {
        int separatorPos = instanceTraceId.indexOf( ":" );
        this.user = new UserKey( instanceTraceId.substring( 0, separatorPos ) );
        this.traceCompletedNumber = new Long( instanceTraceId.substring( separatorPos + 1, instanceTraceId.length() ) );
    }

    public InstantTraceId( final UserKey userKey, final Long traceCompletedNumber )
    {
        this.user = userKey;
        this.traceCompletedNumber = traceCompletedNumber;
    }

    public UserKey getUser()
    {
        return user;
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

        if ( !traceCompletedNumber.equals( that.traceCompletedNumber ) )
        {
            return false;
        }
        if ( !user.equals( that.user ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = user.hashCode();
        result = 31 * result + traceCompletedNumber.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return user.toString() + ":" + traceCompletedNumber;
    }
}
