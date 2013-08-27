/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools.index;

import java.util.List;

import org.joda.time.DateTime;

public interface ReindexContentToolService
{
    public void reindexAllContent( List<String> logEntries );

    public Boolean isReIndexInProgress();

    public void setReIndexInProgress( final Boolean reIndexInProgress );

    public DateTime getLastReindexTime();

    public Long getLastReindexTimeUsed();

    public void setLastReindexFailed( final boolean lastReindexFailed );

    public boolean isLastReindexFailed();

}
