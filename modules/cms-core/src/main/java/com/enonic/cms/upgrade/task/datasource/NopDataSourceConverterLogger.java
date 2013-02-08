package com.enonic.cms.upgrade.task.datasource;

final class NopDataSourceConverterLogger
    implements DataSourceConverterLogger
{
    @Override
    public void logWarning( final String message )
    {
        // Do nothing
    }
}
