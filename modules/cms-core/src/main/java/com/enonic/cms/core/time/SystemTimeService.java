/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.time;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("timeService")
public class SystemTimeService
    extends BaseSystemTimeService
    implements TimeService, InitializingBean
{
    private final Logger LOG = LoggerFactory.getLogger( SystemTimeService.class );

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
