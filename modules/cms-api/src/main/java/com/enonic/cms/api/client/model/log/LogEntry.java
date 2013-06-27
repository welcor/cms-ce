/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.log;

import java.io.Serializable;
import java.util.Date;

public class LogEntry
    implements Serializable
{
    private static final long serialVersionUID = -1L;

    private String logKey;

    private LogEventType eventType;

    private String table;

    private String user;

    private String username;

    private Date timestamp;

    private Integer contentKey;

    private String inetAddress;

    private String path;

    private String title;

    private String site;

    private Integer siteKey;

    public String getLogKey()
    {
        return logKey;
    }

    public void setLogKey( final String logKey )
    {
        this.logKey = logKey;
    }

    public LogEventType getEventType()
    {
        return eventType;
    }

    public void setEventType( final LogEventType eventType )
    {
        this.eventType = eventType;
    }

    public String getTable()
    {
        return table;
    }

    public void setTable( final String table )
    {
        this.table = table;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser( final String user )
    {
        this.user = user;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( final String username )
    {
        this.username = username;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( final Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public Integer getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( final Integer contentKey )
    {
        this.contentKey = contentKey;
    }

    public String getInetAddress()
    {
        return inetAddress;
    }

    public void setInetAddress( final String inetAddress )
    {
        this.inetAddress = inetAddress;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( final String title )
    {
        this.title = title;
    }

    public String getSite()
    {
        return site;
    }

    public void setSite( final String site )
    {
        this.site = site;
    }

    public Integer getSiteKey()
    {
        return siteKey;
    }

    public void setSiteKey( final Integer siteKey )
    {
        this.siteKey = siteKey;
    }
}
