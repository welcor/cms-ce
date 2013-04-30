/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import java.util.Date;

import org.hibernate.Query;

import com.enonic.cms.framework.hibernate.support.SelectBuilder;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * This class represents the specification of a getContentByCategory search.
 */
public class LogEntrySpecification
{

    private UserEntity user;

    private LogType[] types;

    private Table[] tableTypes;

    private boolean allowDuplicateEntries = false;

    private Date dateFilter;

    private DateSpan dateSpan;

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public LogType[] getTypes()
    {
        return types;
    }

    public void setTypes( LogType[] types )
    {
        this.types = types;
    }

    public boolean isAllowDuplicateEntries()
    {
        return allowDuplicateEntries;
    }

    public void setAllowDuplicateEntries( boolean allowDuplicateEntries )
    {
        this.allowDuplicateEntries = allowDuplicateEntries;
    }

    public Date getDateFilter()
    {
        return dateFilter;
    }

    public void setDateFilter( Date dateFilter )
    {
        this.dateFilter = dateFilter;
    }

    public Table[] getTableTypes()
    {
        return tableTypes;
    }

    public void setTableTypes( Table[] tableTypes )
    {
        this.tableTypes = tableTypes;
    }

    public void setDateSpan( Date publishFrom, Date publishTo )
    {
        this.dateSpan = new DateSpan( publishFrom, publishTo );
    }

    public void appendDateSpan( String alias, SelectBuilder hqlQuery )
    {
        dateSpan.appendSpan( alias, hqlQuery );
    }

    public boolean isDateSpanSet()
    {
        return dateSpan != null && dateSpan.isSpanSet();
    }

    public void setDateSpanParameters( Query compiled )
    {
        dateSpan.setSpanParameters( compiled );
    }

    private class DateSpan
    {
        private final Date publishFrom;

        private final Date publishTo;

        private DateSpan( final Date publishFrom, final Date publishTo )
        {
            this.publishFrom = publishFrom;
            this.publishTo = publishTo;
        }

        private boolean isSpanSet()
        {
            return publishTo != null;
        }

        private void appendSpan( String alias, SelectBuilder hqlQuery )
        {
            if ( publishFrom != null )
            {
                hqlQuery.addFilter( "AND", alias + ".timestamp >= :publishFrom" );
            }

            hqlQuery.addFilter( "AND", alias + ".timestamp <= :publishTo" );
        }

        private void setSpanParameters( Query compiled )
        {
            if ( publishFrom != null )
            {
                compiled.setTimestamp( "publishFrom", publishFrom );
            }

            compiled.setTimestamp( "publishTo", publishTo );
        }
    }
}