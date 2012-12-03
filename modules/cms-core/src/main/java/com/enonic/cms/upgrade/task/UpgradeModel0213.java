package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0213
    extends AbstractUpgradeTask
{
    public UpgradeModel0213()
    {
        super( 213 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "upgrade 213" );

        throw new RuntimeException( "fail" );
    }
}
