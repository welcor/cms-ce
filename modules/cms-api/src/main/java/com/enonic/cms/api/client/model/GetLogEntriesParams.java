/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.util.Date;

public class GetLogEntriesParams
    extends AbstractParams
{
    private static final long serialVersionUID = -1L;

    /**
     * Obtain log entries with timestamp equal or later than the specified time.
     */
    public Date from = null;

    /**
     * Obtain log entries with timestamp equal or earlier than the specified time.
     * Default is the current time.
     */
    public Date to = new Date();

    /**
     * Specifies the maximum number of log entries to include in the result.
     * Default is 100.
     */
    public int count = 100;
}
