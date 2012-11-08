/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.time;

import org.joda.time.DateTime;

/**
 * Jul 2, 2010
 */
public class MockTimeService
    extends BaseSystemTimeService
    implements TimeService
{
    private DateTime bootTime;

    private DateTime timeNow;

    public MockTimeService()
    {
        // Default contructor
    }

    public MockTimeService( DateTime timeNow )
    {
        this.timeNow = timeNow;
    }

    public void setBootTime( final DateTime bootTime )
    {
        this.bootTime = bootTime;
    }

    public void setTimeNow( DateTime value )
    {
        this.timeNow = value;
    }

    public DateTime getNowAsDateTime()
    {
        checkTimeNowConfigured();
        return timeNow;
    }

    public long getNowAsMilliseconds()
    {
        checkTimeNowConfigured();
        return timeNow.getMillis();
    }

    @Override
    public DateTime bootTime()
    {
        return bootTime;
    }

    private void checkTimeNowConfigured()
    {
        if ( timeNow == null )
        {
            throw new IllegalStateException( "MockTimeService have not been configured with time for now" );
        }
    }
}
