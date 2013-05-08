package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverter;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverterUpgradeModel206;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverterLoggerImpl;

final class UpgradeModel0206
    extends AbstractDataSourceUpgradeTask
{
    public UpgradeModel0206()
    {
        super( 206 );
    }

    @Override
    protected DataSourceConverter newConverter( final UpgradeContext context )
    {
        return new DataSourceConverterUpgradeModel206( new DataSourceConverterLoggerImpl( context ) );
    }

    @Override
    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Converting datasource xml to a simpler format." );
        upgradeDataSources( context );
    }
}
