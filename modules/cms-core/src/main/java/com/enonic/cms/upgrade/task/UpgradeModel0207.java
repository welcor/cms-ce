package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverter;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverter2;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverterLogger;

final class UpgradeModel0207
    extends AbstractDataSourceUpgradeTask
{
    public UpgradeModel0207()
    {
        super( 207 );
    }

    @Override
    protected DataSourceConverter newConverter( final UpgradeContext context )
    {
        return new DataSourceConverter2( new DataSourceConverterLogger()
        {
            @Override
            public void logWarning( final String message )
            {
                context.logWarning( message );
            }
        } );
    }

    @Override
    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Converting datasources to named parameters." );
        upgradeDataSources( context );
    }
}
