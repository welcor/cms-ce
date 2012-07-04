/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.time;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * Jun 18, 2009
 */
@Service("timeService")
public class SystemTimeService
    implements TimeService
{
    public DateTime getNowAsDateTime()
    {
        return new DateTime();
    }

    public long getNowAsMilliseconds()
    {
        return new DateTime().getMillis();
    }
}
