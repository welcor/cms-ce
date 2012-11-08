package com.enonic.cms.core.time;

import org.joda.time.DateTime;
import org.joda.time.Period;

public abstract class BaseSystemTimeService
    implements TimeService
{
    public abstract DateTime getNowAsDateTime();

    public long getNowAsMilliseconds()
    {
        return getNowAsDateTime().getMillis();
    }

    @Override
    public Period upTime()
    {
        Period period = new Period( bootTime(), getNowAsDateTime() );
        if ( period.getMillis() >= 500 )
        {
            period = period.minusMillis( period.getMillis() ).plusSeconds( 1 );
        }
        else if ( period.getMillis() < 500 )
        {
            period = period.minusMillis( period.getMillis() );
        }
        return period;
    }

}
