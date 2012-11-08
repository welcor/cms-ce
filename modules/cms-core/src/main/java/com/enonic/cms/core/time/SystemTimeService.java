/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.time;

import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("timeService")
public class SystemTimeService
    extends BaseSystemTimeService
    implements TimeService, InitializingBean
{
    private final Logger LOG = Logger.getLogger( SystemTimeService.class.getName() );

    private DateTime bootTime;

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        this.bootTime = DateTime.now();
        LOG.info( "System Boot Time noted as: " + this.bootTime );
    }

    public DateTime getNowAsDateTime()
    {
        return DateTime.now();
    }

    @Override
    public DateTime bootTime()
    {
        return bootTime;
    }
}
