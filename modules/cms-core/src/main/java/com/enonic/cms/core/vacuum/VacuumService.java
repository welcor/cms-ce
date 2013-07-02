/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.vacuum;

public interface VacuumService
{
    /**
     * Clean read logs.
     */
    void cleanReadLogs();

    /**
     * Clean unused content.
     */
    void cleanUnusedContent();

    /**
     * returns progress info about either Clean unused content or Clean read logs.
     */
    ProgressInfo getProgressInfo();
}
