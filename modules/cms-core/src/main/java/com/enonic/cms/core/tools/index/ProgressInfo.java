/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools.index;


/**
 * DTO for the indexing progress
 */
public class ProgressInfo
{
    public static final ProgressInfo NONE = new ProgressInfo();

    private volatile int percent;

    private volatile String logLine;

    private volatile boolean inProgress;

    private transient int interval;

    public ProgressInfo()
    {
    }

    public int getPercent()
    {
        return percent;
    }

    public void setPercent( final int percent )
    {
        this.percent = percent;
    }

    public String getLogLine()
    {
        return logLine;
    }

    public void setLogLine( final String logLine )
    {
        this.logLine = logLine;
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval( final int interval )
    {
        this.interval = interval;
    }

    public void setInProgress( final boolean inProgress )
    {
        this.inProgress = inProgress;
    }

    public boolean isInProgress()
    {
        return inProgress;
    }
}
