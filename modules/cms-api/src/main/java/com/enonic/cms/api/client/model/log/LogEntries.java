package com.enonic.cms.api.client.model.log;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class LogEntries
    implements Serializable, Iterable<LogEntry>
{
    private static final long serialVersionUID = -1L;

    private final List<LogEntry> logEntries;

    public LogEntries( final List<LogEntry> logEntries )
    {
        this.logEntries = logEntries;
    }

    public LogEntry getLogEntry( final int index )
    {
        return this.logEntries.get( index );
    }

    public int getCount()
    {
        return logEntries.size();
    }

    @Override
    public Iterator<LogEntry> iterator()
    {
        return logEntries.iterator();
    }
}
