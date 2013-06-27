/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content;

import java.util.Date;

import com.enonic.cms.core.log.LogEntryResultSet;
import com.enonic.cms.core.log.LogEntrySpecification;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.security.user.UserEntity;


public class GetLogEntryExecutor
{
    private final LogService logService;

    private UserEntity user;

    private String orderBy = "timestamp ASC";

    private int count;

    private Date publishFrom;

    private Date publishTo;

    public GetLogEntryExecutor( LogService logService )
    {
        this.logService = logService;
    }

    public GetLogEntryExecutor user( UserEntity value )
    {
        this.user = value;
        return this;
    }

    public GetLogEntryExecutor orderBy( String value )
    {
        this.orderBy = value;
        return this;
    }

    public GetLogEntryExecutor count( int value )
    {
        this.count = value;
        return this;
    }

    public GetLogEntryExecutor publishFrom( Date publishFrom )
    {
        this.publishFrom = publishFrom;
        return this;
    }

    public GetLogEntryExecutor publishTo( Date publishTo )
    {
        this.publishTo = publishTo;
        return this;
    }

    public LogEntryResultSet execute()
    {
        LogEntrySpecification logSpecification = new LogEntrySpecification();
        if ( user != null )
        {
            logSpecification.setUser( user );
        }
        logSpecification.setAllowDuplicateEntries( true );
        logSpecification.setDateSpan( publishFrom, publishTo );

        return logService.getLogEntries( logSpecification, orderBy, count, 0 );
    }
}
