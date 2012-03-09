package com.enonic.vertical.work;

import java.util.Properties;

public interface WorkRunner
{
    public void executeWork( String className, Properties props )
        throws Exception;
}
