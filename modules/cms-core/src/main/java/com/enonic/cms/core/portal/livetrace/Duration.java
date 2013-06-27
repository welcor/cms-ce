/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.common.base.Preconditions;

/**
 * Oct 11, 2010
 */
public class Duration
    extends SimpleDuration
{
    private DateTime startTime;

    private DateTime stopTime;

    public boolean hasStarted()
    {
        return startTime != null;
    }

    public DateTime getStartTime()
    {
        return startTime;
    }

    void setStartTime( DateTime time )
    {
        this.startTime = time;
    }

    public boolean hasEnded()
    {
        return stopTime != null;
    }

    public DateTime getStopTime()
    {
        return stopTime;
    }

    void setStopTime( DateTime stopTime )
    {
        Preconditions.checkNotNull( startTime );
        Preconditions.checkNotNull( stopTime );
        this.stopTime = stopTime;
        this.setDurationInMilliseconds( stopTime.getMillis() - startTime.getMillis() );
    }

    @Override
    @JsonIgnore
    public long getAsMilliseconds()
    {
        if ( hasEnded() )
        {
            return super.getAsMilliseconds();
        }
        else if ( startTime != null )
        {
            return new DateTime().getMillis() - startTime.getMillis();
        }
        else
        {
            return 0L;
        }
    }

    @Override
    public String getAsHRFormat()
    {
        if ( hasEnded() )
        {
            return super.getAsHRFormat();
        }
        else if ( startTime != null )
        {
            return HOURS_MINUTES_MILLIS.print( new Period( startTime, new DateTime() ) );
        }
        else
        {
            return "0";
        }
    }

    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "startTime: " ).append( startTime ).append( "\n" );
        s.append( "stopTime: " ).append( stopTime ).append( "\n" );
        s.append( "executionTimeInMilliseconds: " ).append( getAsMilliseconds() ).append( "\n" );
        return s.toString();
    }
}
