package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0212
    extends AbstractUpgradeTask
{
    public UpgradeModel0212()
    {
        super( 212 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "upgrade 212" );
    }
}
