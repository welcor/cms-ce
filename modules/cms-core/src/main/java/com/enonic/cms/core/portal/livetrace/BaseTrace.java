/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import org.joda.time.DateTime;

public abstract class BaseTrace
    implements Trace
{
    Traces container;

    private Duration duration = new Duration();

    @Override
    public void setContainer( Traces container )
    {
        this.container = container;
    }

    void setStartTime( DateTime startTime )
    {
        duration.setStartTime( startTime );
    }

    void setStopTime( DateTime stopTime )
    {
        duration.setStopTime( stopTime );
        if ( container != null )
        {
            container.computeTotalPeriod();
        }
    }

    public Duration getDuration()
    {
        return duration;
    }

}

