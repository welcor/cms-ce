/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work;

import java.util.Properties;

public interface WorkRunner
{
    public void executeWork( String className, Properties props )
        throws Exception;
}
