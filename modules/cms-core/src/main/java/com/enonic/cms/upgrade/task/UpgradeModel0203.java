package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0203
    extends AbstractUpgradeTask
{
    public UpgradeModel0203()
    {
        super( 203 );
    }

    @Override
    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Dropping content-index table in database" );
        context.dropTable( "tContentIndex" );
    }
}
