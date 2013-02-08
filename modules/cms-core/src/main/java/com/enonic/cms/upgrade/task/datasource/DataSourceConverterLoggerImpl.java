package com.enonic.cms.upgrade.task.datasource;

import com.enonic.cms.upgrade.UpgradeContext;

public final class DataSourceConverterLoggerImpl
    implements DataSourceConverterLogger
{
    private final UpgradeContext context;

    public DataSourceConverterLoggerImpl( final UpgradeContext context )
    {
        this.context = context;
    }

    @Override
    public void logWarning( final String message )
    {
        this.context.logWarning( message );
    }
}
