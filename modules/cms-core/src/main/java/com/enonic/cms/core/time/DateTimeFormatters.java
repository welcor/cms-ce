/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.time;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeFormatters
{
    public static final DateTimeFormatter DATE_TIME = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss" );
}
