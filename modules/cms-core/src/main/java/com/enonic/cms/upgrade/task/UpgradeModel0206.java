package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverter;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverter1;

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
        return new DataSourceConverter1();
    }

    @Override
    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Converting datasource xml to a simpler format." );
        upgradeDataSources( context );
    }
}
