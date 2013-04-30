package com.enonic.cms.api.client.model.log;

import java.io.Serializable;
import java.util.Date;

public class LogEntry
    implements Serializable
{
    private static final long serialVersionUID = -1L;

    public enum LogEventType
    {
        LOGIN,
        LOGIN_USERSTORE,
        LOGIN_FAILED,
        LOGOUT,
        ENTITY_CREATED,
        ENTITY_UPDATED,
        ENTITY_REMOVED,
        ENTITY_OPENED
    }

    public String logKey;

    public LogEventType eventType;

    public String table;

    public String user;

    public String username;

    public Date timestamp;

    public Integer contentKey;

    public String inetAddress;

    public String path;

    public String title;

    public String site;

    public Integer siteKey;
}
